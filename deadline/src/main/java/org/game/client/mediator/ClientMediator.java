package org.game.client.mediator;

import lombok.extern.slf4j.Slf4j;
import org.game.client.Client;
import org.game.client.GameState;
import org.game.entity.*;
import org.game.entity.powerup.PowerUp;
import org.game.entity.powerup.PowerUpType;
import org.game.json.Json;
import org.game.message.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.game.json.JsonLabelPair.labelPair;

@Slf4j
public final class ClientMediator implements Mediator {

    private static final int PORT = 9000;

    private final Client client;
    private final GameState gameState;
    private final GameView view;

    private final Json json = new Json();

    private SocketChannel socketChannel;
    private Selector selector;

    private final ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 8);
    private final Queue<ByteBuffer> pendingWrites = new ConcurrentLinkedQueue<>();

    // server → mediator message queue
    private final Queue<Message> incomingMessages = new ConcurrentLinkedQueue<>();

    private volatile boolean running = false;

    public ClientMediator( Client client,
                          GameState gameState,
                          GameView view) {
        this.client = client;
        this.gameState = gameState;
        this.view = view;
    }

    // ---------------- Mediator API (UI -> server) ----------------

    @Override
    public void onPlayerMove(int dx, int dy) {
        MoveMessage moveMessage = new MoveMessage(client.getClientId(), dx, dy);
        sendLocalInput(json.toJson(moveMessage, labelPair(Message.JSON_LABEL, "move")));
    }

    @Override
    public void onPlayerShoot(UUID projectileId) {
        var player = gameState.getPlayer(client.getClientId());
        if (player == null) return;

        var localProj = gameState.getProjectiles().get(projectileId);
        if (localProj == null) return;

        ProjectileSpawnMessage proj = new ProjectileSpawnMessage(
                localProj.getGlobalX(),
                localProj.getGlobalY(),
                localProj.getDirection(),
                projectileId,
                client.getClientId(),
                localProj.getSpeed(),
                localProj.getDamage(),
                localProj.getMaxDistance()
        );

        sendLocalInput(json.toJson(proj, labelPair(Message.JSON_LABEL, "projectileSpawn")));
    }

    @Override
    public void onEnemyHealthChanged(Enemy enemy) {
        EnemyHealthUpdateMessage msg =
                new EnemyHealthUpdateMessage(enemy.getId(), enemy.getHitPoints());
        sendLocalInput(json.toJson(msg, labelPair(Message.JSON_LABEL, "enemyHealth")));
    }

    @Override
    public void onPowerUpPicked(PowerUp powerUp) {
        PowerUpRemoveMessage msg = new PowerUpRemoveMessage(powerUp.getId());
        sendLocalInput(json.toJson(msg, labelPair(Message.JSON_LABEL, "powerUpRemove")));
    }


    @Override
    public void onServerMessage(Message message) {
        incomingMessages.offer(message);
    }

    @Override
    public void processServerMessagesForFrame() {
        int processed = 0;
        int maxMsgPerTick = 100;
        Message msg;

        while (processed < maxMsgPerTick && (msg = incomingMessages.poll()) != null) {
            switch (msg) {
                case JoinMessage(UUID playerId, ClassType playerClass, String name, int x, int y) -> {
                    gameState.addPlayer(playerId, playerClass, name, x, y);
                    if (playerId.equals(client.getClientId())) {
                        view.onLocalPlayerJoined(playerClass, x, y);
                    }
                }
                case LeaveMessage(UUID playerId) -> gameState.removePlayer(playerId);
                case MoveMessage(UUID playerId, int x, int y) -> {
                    if (!playerId.equals(client.getClientId())) {
                        Player player = gameState.getPlayer(playerId);
                        if (player != null) {
                            player.updateFromServer(x, y);
                        }
                    } else {
                        view.onLocalPlayerMoveFromServer(x, y);
                    }
                }
                case EnemySpawnMessage(long enemyId, EnemyType type, EnemySize size, int newX, int newY) ->
                        gameState.spawnEnemyFromServer(enemyId, type, size, newX, newY);
                case EnemyRemoveMessage(long enemyId) -> gameState.removeEnemy(enemyId);
                case EnemyMoveMessage(long enemyId, int newX, int newY) ->
                        gameState.updateEnemyPosition(enemyId, newX, newY);
                case ProjectileSpawnMessage(int startX, int startY, FramePosition dir, UUID projId, UUID playerId,
                                            int speed, int damage, double maxDistance) ->
                        gameState.spawnProjectile(projId, playerId, startX, startY, dir, speed, damage, maxDistance);
                case EnemyBulkCopyMessage(Map<Long, EnemyCopy> enemies) ->
                        gameState.copyAllEnemies(enemies);
                case EnemyHealthUpdateMessage(long enemyId, int newHealth) -> {
                    Enemy enemy = gameState.getEnemies().get(enemyId);
                    if (enemy != null) {
                        enemy.setHitPoints(newHealth);
                    }
                }
                case PlayerHealthUpdateMessage(UUID playerId, int newHealth) -> {
                    Player player = gameState.getPlayer(playerId);
                    if (player != null) {
                        player.setHitPoints(newHealth);
                    }
                }
                case PlayerRespawnMessage(UUID playerId, int respawnX, int respawnY) -> {
                    Player player = gameState.getPlayer(playerId);
                    if (player != null) {
                        player.setGlobalX(respawnX);
                        player.setGlobalY(respawnY);
                        player.setHitPoints(player.getMaxHitPoints());
                    }
                    if (playerId.equals(client.getClientId())) {
                        view.onLocalPlayerRespawn(respawnX, respawnY);
                    }
                }
                case PowerUpRemoveMessage(long powerUpId) -> gameState.removePowerUp(powerUpId);
                case PowerUpSpawnMessage(long powerUpId, PowerUpType powerUp, int x, int y) ->
                        gameState.spawnPowerUp(powerUpId, powerUp, x, y);
                case PlayerStatsUpdateMessage(UUID playerId,
                                              int hitPoints, int maxHitPoints, int attack, int speed) -> {
                    Player player = gameState.getPlayer(playerId);
                    if (player != null) {
                        player.setHitPoints(hitPoints);
                        player.setMaxHitPoints(maxHitPoints);
                        player.setAttack(attack);
                        player.setSpeed(speed);
                    }
                }
                case PlayerDefenseUpdateMessage(UUID playerId,
                                                int armorCount, boolean isShieldActive) ->
                        gameState.updatePlayerShields(playerId, armorCount, isShieldActive);
            }
            processed++;
        }
    }

    // ---------------- Lifecycle ----------------

    @Override
    public void start() {
        running = true;

        Thread.ofVirtual()
                .name("ClientNetworkThread : " + client.getPlayerName())
                .start(() -> {
                    try {
                        connectToServer();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
    }

    @Override
    public void shutdown() {
        running = false;
        try {
            if (selector != null) selector.close();
            if (socketChannel != null) socketChannel.close();
        } catch (IOException e) {
            log.error("Error closing network resources", e);
        }
    }

    private void connectToServer() throws InterruptedException {
        while (running) {
            try {
                selector = Selector.open();
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);

                socketChannel.connect(new InetSocketAddress(PORT));
                socketChannel.register(selector, SelectionKey.OP_CONNECT);

                networkLoop();

            } catch (IOException e) {
                log.error("Failed to connect to server on port {}. Retrying...", PORT);
                Thread.sleep(600);
            } finally {
                try {
                    if (socketChannel != null) socketChannel.close();
                    if (selector != null) selector.close();
                } catch (IOException e) {
                    log.error("Error closing resources", e);
                }
            }
        }
    }

    private void networkLoop() throws IOException {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                selector.select(500);
            } catch (ClosedSelectorException cse) {
                break;
            }

            var selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();
                if (!key.isValid()) continue;

                if (key.isConnectable()) {
                    finishConnect(key);
                } else if (key.isReadable()) {
                    doRead(key);
                } else if (key.isWritable()) {
                    doWrite(key);
                }
            }

            SelectionKey key = (socketChannel != null) ? socketChannel.keyFor(selector) : null;
            if (key != null && key.isValid() && !pendingWrites.isEmpty()) {
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            }
        }
    }

    private void sendLocalInput(String payload) {
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        ByteBuffer writeBuffer = ByteBuffer.allocate(4 + bytes.length);
        writeBuffer.putInt(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();

        pendingWrites.add(writeBuffer);

        if (selector != null) {
            selector.wakeup();
        }
    }

    private void doWrite(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            for (; ; ) {
                ByteBuffer writeBuffer = pendingWrites.peek();
                if (writeBuffer == null) break;

                sc.write(writeBuffer);
                if (writeBuffer.hasRemaining()) {
                    break;
                }
                pendingWrites.poll();
            }
        } catch (IOException e) {
            key.cancel();
            sc.close();
            return;
        }

        if (pendingWrites.isEmpty() && key.isValid()) {
            int ops = (key.interestOps() & ~SelectionKey.OP_WRITE) | SelectionKey.OP_READ;
            key.interestOps(ops);
        }
    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        int read = sc.read(readBuffer);
        if (read == -1) {
            key.cancel();
            sc.close();
            return;
        }
        readBuffer.flip();
        for (; ; ) {
            if (readBuffer.remaining() < 4) break;
            readBuffer.mark();
            int length = readBuffer.getInt();
            if (readBuffer.remaining() < length) {
                readBuffer.reset();
                break;
            }
            byte[] messageBytes = new byte[length];
            readBuffer.get(messageBytes);
            String jsonPayload = new String(messageBytes, StandardCharsets.UTF_8);

            Message message = json.fromJson(jsonPayload, Message.class);
            onServerMessage(message);
        }
        readBuffer.compact();
    }

    private void finishConnect(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        if (sc.finishConnect()) {
            log.debug("Connected to server");

            int ops = SelectionKey.OP_READ;
            if (!pendingWrites.isEmpty()) {
                ops |= SelectionKey.OP_WRITE;
            }
            key.interestOps(ops);

            Message message = new JoinMessage(client.getClientId(), client.getChosenClass(), client.getPlayerName());
            sendLocalInput(json.toJson(message, labelPair(Message.JSON_LABEL, "join")));
        } else {
            key.cancel();
        }
    }
}
