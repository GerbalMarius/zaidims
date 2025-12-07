package org.game.server;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.game.entity.*;
import org.game.entity.powerup.ArmorPowerUp;
import org.game.entity.powerup.PowerUp;
import org.game.entity.powerup.PowerUpType;
import org.game.entity.decorator.AttackDecorator;
import org.game.entity.decorator.MaxHpDecorator;
import org.game.entity.decorator.SpeedDecorator;
import org.game.entity.powerup.ShieldPowerUp;
import org.game.json.Json;
import org.game.message.*;
import org.game.tiles.TileManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.game.json.JsonLabelPair.labelPair;
import static org.game.server.Server.ServerActions.*;

@Slf4j
public final class Server {

    private static final int PORT = 9000;
    private Selector selector;
    private ServerSocketChannel serverChannel;


    @Getter
    private final Map<SocketChannel, ClientState> clients = new LinkedHashMap<>();

    @Getter
    private final Map<Long, Enemy> enemies = new ConcurrentHashMap<>();

    @Getter
    private final Map<Long, PowerUp> powerUps = new ConcurrentHashMap<>();

    private static boolean firstPlayer = true;

    @Getter
    private final GameWorldFacade gameWorld = new GameWorldFacade(this);

    @Setter
    private boolean adminMode = false;

    @Getter
    private final CollisionChecker entityChecker = new CollisionChecker(new TileManager());

    private final ScheduledExecutorService playerHpRegenTick = Executors.newSingleThreadScheduledExecutor();


    @Getter
    private final Json json = new Json();


    static void main() throws IOException {
        new Server().start();
    }

    public void start() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();

        serverChannel.configureBlocking(false);

        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("Server started on port: " + PORT);

        for (; ; ) {

            if (selector.select(250) == 0) {
                continue;
            }
            var keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                try {
                    if (key.isAcceptable()) accept();
                    else if (key.isReadable()) read(key);
                    else if (key.isWritable()) write(key);
                } catch (IOException e) {
                    closeKey(key);
                    log.error("Error handling key", e);
                }
            }
        }
    }

    private void accept() throws IOException {
        SocketChannel sc = serverChannel.accept();
        sc.configureBlocking(false);
        sc.socket().setTcpNoDelay(true);


        SelectionKey sk = sc.register(selector, SelectionKey.OP_READ);
        ClientState cs = new ClientState();
        sk.attach(cs);
        clients.put(sc, cs);
        log.info("Accepted  from : {}", sc.getRemoteAddress());

        if (firstPlayer && !adminMode) {

            gameWorld.startSpawningIndividualEnemies(0, 10, TimeUnit.SECONDS);
            gameWorld.startSpawningWaves(10, 30, TimeUnit.SECONDS);
            gameWorld.startUpdatingEnemyPos(0, 50, TimeUnit.MILLISECONDS);

            gameWorld.startDispensingPowerUps(10, 15, TimeUnit.SECONDS);

            startPlayerRegen();
            firstPlayer = false;
            return;
        }
        Map<Long, EnemyCopy> enemyCopies = enemies.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, endData -> {
                    Enemy value = endData.getValue();
                    return new EnemyCopy(
                            endData.getKey(), value.getType(),
                            value.getSize(), value.getGlobalX(),
                            value.getGlobalY(), value.getHitPoints());
                }));

        sendTo(sc, json.toJson(new EnemyBulkCopyMessage(enemyCopies), labelPair(Message.JSON_LABEL, "enemyCopy")));

    }

    private void startPlayerRegen() {
        playerHpRegenTick.scheduleAtFixedRate(() -> {
            try {
                regenAllPlayers();
            } catch (Exception e) {
                log.error("Regen task error", e);
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void regenAllPlayers() {
        long now = System.currentTimeMillis();
        for (var entry : clients.entrySet()) {
            ClientState cs = entry.getValue();
            Player player = cs.getPlayer();
            if (player == null) continue;
            boolean changed = player.regenIfNeeded(now);
            if (changed) {
                var healthMsg = new PlayerHealthUpdateMessage(cs.getId(), player.getHitPoints());
                broadcast(json.toJson(healthMsg, labelPair(Message.JSON_LABEL, "playerHealth")));
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();

        ByteBuffer readBuffer = state.getReadBuffer();


        int read = sc.read(readBuffer);
        if (read == -1) { // client closed
            closeKey(key);
            return;
        }

        readBuffer.flip();
        while (true) {
            int messageLength;

            if (!state.isReading()) {
                if (readBuffer.remaining() >= 4) {
                    messageLength = readBuffer.getInt();

                    state.setMessageLength(messageLength);
                    state.setReading(true);


                    if (messageLength <= 0 || messageLength > 10_000_000) {
                        closeKey(key);
                        return;
                    }
                } else {
                    break;
                }
            }

            messageLength = state.getMessageLength();
            if (readBuffer.remaining() >= messageLength) {
                byte[] msgBytes = new byte[messageLength];
                readBuffer.get(msgBytes);
                String message = new String(msgBytes, StandardCharsets.UTF_8);

                onMessage(sc, json.fromJson(message, Message.class));

                state.setReading(false);
                state.setMessageLength(0);
            } else {
                break;
            }
        }
        readBuffer.compact();
    }

    private void onMessage(SocketChannel from, Message message) {
        ClientState state = clients.get(from);

        switch (message) {
            case JoinMessage(UUID playerId, ClassType playerClass, String playerName, int _, int _) ->
                    createPlayer(from, this, playerId, playerClass, playerName, state);
            case MoveMessage(UUID id, int dx, int dy) -> movePlayer(id, this, dx, dy, state);
            case LeaveMessage leaveMessage ->
                    broadcast(json.toJson(leaveMessage, labelPair(Message.JSON_LABEL, "leave")));
            case EnemyMoveMessage _, EnemyRemoveMessage _, EnemySpawnMessage _, EnemyBulkCopyMessage _ -> {

            }
            case ProjectileSpawnMessage projSpawn ->
                    broadcast(json.toJson(projSpawn, labelPair(Message.JSON_LABEL, "projectileSpawn")));
            case EnemyHealthUpdateMessage(long eId, int newHealth) -> {
                Enemy enemy = enemies.get(eId);
                if (enemy != null) {
                    enemy.setHitPoints(newHealth);

                    if (newHealth <= 0) {
                        enemies.remove(eId);
                        broadcast(json.toJson(new EnemyRemoveMessage(eId), labelPair(Message.JSON_LABEL, "enemyRemove")));
                    }

                    broadcast(json.toJson(message, labelPair(Message.JSON_LABEL, "enemyHealth")));
                }
            }
            case PlayerRespawnMessage _, PowerUpSpawnMessage _, PlayerStatsUpdateMessage _-> {
            }

            case PowerUpRemoveMessage(long powerUpId) -> applyPowerUp(from, message, powerUpId);
            case PlayerHealthUpdateMessage(UUID playerId, int newHealth) -> {
                var client = clients.values().stream()
                        .filter(c -> c.getId().equals(playerId))
                        .findFirst()
                        .orElse(null);

                if (client != null) {
                    Player player = client.getPlayer();
                    player.setHitPoints(newHealth);

                    if (newHealth <= 0) {
                        int respawnX = WorldSettings.CENTER_X;
                        int respawnY = WorldSettings.CENTER_Y;

                        player.setGlobalX(respawnX);
                        player.setGlobalY(respawnY);
                        player.setHitPoints(player.getMaxHitPoints());

                        PlayerRespawnMessage respawnMsg = new PlayerRespawnMessage(playerId, respawnX, respawnY);
                        broadcast(json.toJson(respawnMsg, labelPair(Message.JSON_LABEL, "playerRespawn")));
                    }

                    broadcast(json.toJson(message, labelPair(Message.JSON_LABEL, "playerHealth")));
                }
            }

            case PlayerDefenseUpdateMessage defMsg -> broadcast(json.toJson(defMsg, labelPair(Message.JSON_LABEL, "playerDefense")));
        }
    }

    private void applyPowerUp(SocketChannel from, Message message, long powerUpId) {
        PowerUp powerUp = powerUps.get(powerUpId);
        if (powerUp == null) return;

        ClientState cs = clients.get(from);
        if (cs == null || cs.getPlayer() == null) return;

        Player player = cs.getPlayer();

        switch (powerUp.getType()) {
            case ATTACK -> cs.setPlayer(new AttackDecorator(player, 5));
            case SPEED -> cs.setPlayer(new SpeedDecorator(player, 1));
            case MAX_HP -> cs.setPlayer(new MaxHpDecorator(player, 10));
            case SHIELD -> ((ShieldPowerUp)powerUp).applyTo(player);
            case ARMOR -> ((ArmorPowerUp)powerUp).applyTo(player);
        }

        powerUps.remove(powerUpId);


        broadcast(json.toJson(message, labelPair(Message.JSON_LABEL, "powerUpRemove")));

        Player decorated = cs.getPlayer();
        PlayerStatsUpdateMessage statsMsg = new PlayerStatsUpdateMessage(
                cs.getId(),
                decorated.getHitPoints(),
                decorated.getMaxHitPoints(),
                decorated.getAttack(),
                decorated.getSpeed()
        );

        broadcast(json.toJson(statsMsg, labelPair(Message.JSON_LABEL, "playerStats")));

        broadcastDefense(decorated, cs.getId());
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ClientState cs = (ClientState) key.attachment();


        while (!cs.noMoreMessages()) {
            ByteBuffer bb = cs.peekMessage();
            sc.write(bb);
            if (bb.hasRemaining()) {
                break;
            }
            cs.pollMessage();
        }

        if (cs.noMoreMessages()) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
        }
    }


    private void sendTo(SocketChannel sc, String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4 + bytes.length);
        buf.putInt(bytes.length);
        buf.put(bytes);
        buf.flip();

        ClientState cs = clients.get(sc);
        if (cs == null) return;


        ByteBuffer copy = ByteBuffer.allocate(buf.remaining());
        copy.put(buf);
        copy.flip();
        cs.enqueueWrite(copy);

        SelectionKey key = sc.keyFor(selector);
        if (key != null && key.isValid()) {
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }
        selector.wakeup();
    }

    private void closeKey(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        ClientState cs = (ClientState) key.attachment();

        String remote = "";
        try {
            remote = sc.getRemoteAddress().toString();
        } catch (IOException e) {
            log.error("ERROR GETTING REMOTE ADDRESS");
        }
        log.debug("Closing {}", remote);

        if (cs != null && cs.getId() != null) {
            LeaveMessage leaveMessage = new LeaveMessage(cs.getId());
            broadcast(json.toJson(leaveMessage, labelPair(Message.JSON_LABEL, "leave")));
        }

        clients.remove(sc);
        try {
            sc.close();
        } catch (IOException e) {
            log.error("Error closing client {}", remote);
        }
        key.cancel();
    }

    private void broadcast(String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4 + bytes.length);
        buf.putInt(bytes.length);
        buf.put(bytes);
        buf.flip();

        for (var clientSockets : clients.entrySet()) {
            SocketChannel sc = clientSockets.getKey();
            ClientState cs = clientSockets.getValue();


            //Write a message copy for each client
            ByteBuffer copy = ByteBuffer.allocate(buf.remaining());
            copy.put(buf.slice());
            copy.flip();
            cs.enqueueWrite(copy);
            SelectionKey key = sc.keyFor(selector);
            if (key != null && key.isValid()) {
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            }
        }

        selector.wakeup();

    }

    private void broadcastDefense(Player player, UUID playerId) {
        var defMsg = new PlayerDefenseUpdateMessage(
                playerId,
                player.getArmorCount(),
                player.isShieldActive()
        );
        broadcast(json.toJson(defMsg, labelPair(Message.JSON_LABEL, "playerDefense")));
    }

    public void sendToAll(String msg) {
        broadcast(msg);
    }

    public void respawnPlayer(UUID playerId, int respawnX, int respawnY) {

        ClientState cs = clients.values().stream()
                .filter(c -> c.getId().equals(playerId))
                .findFirst()
                .orElse(null);

        if (cs == null) return;

        cs.setX(respawnX);
        cs.setY(respawnY);

        var respawnMsg = new PlayerRespawnMessage(playerId, respawnX, respawnY);
        sendToAll(json.toJson(respawnMsg, labelPair(Message.JSON_LABEL, "playerRespawn")));

        var moveMsg = new MoveMessage(playerId, respawnX, respawnY);
        sendToAll(json.toJson(moveMsg, labelPair(Message.JSON_LABEL, "move")));

        log.debug("Player with ID {} respawned ({}, {})", playerId, respawnX, respawnY);
    }


    public final static class ServerActions {

        private ServerActions() {
        }


        public static void createPlayer(SocketChannel from,
                                        Server server,
                                        UUID playerId,
                                        ClassType playerClass,
                                        String playerName,
                                        ClientState state) {
            state.setId(playerId);
            state.setPlayerClass(playerClass);
            state.setName(playerName);


            Player newPlayer = new Player(playerClass, playerName, state.getX(), state.getY());

            state.setPlayer(newPlayer);

            Collection<ClientState> states = server.clients.values();

            state.setX(WorldSettings.CENTER_X);
            state.setY(WorldSettings.CENTER_Y);

            for (ClientState other : states) {
                if (other.getId() != null && state != other) {
                    var join = new JoinMessage(other.getId(), other.getPlayerClass(), other.getName(), other.getX(), other.getY());
                    server.sendTo(from, server.json.toJson(join, labelPair(Message.JSON_LABEL, "join")));
                }
            }

            JoinMessage join = new JoinMessage(state.getId(), state.getPlayerClass(), state.getName(), state.getX(), state.getY());
            server.broadcast(server.json.toJson(join, labelPair(Message.JSON_LABEL, "join")));
        }


        public static void movePlayer(UUID id, Server server, int dx, int dy, ClientState state) {
            if (state.getId() == null || !state.getId().equals(id)) {
                log.debug("Spoofed MOVE ignored");
                return;
            }

            state.setX(state.getX() + dx);
            state.setY(state.getY() + dy);

            if (state.getPlayer() != null) {
                state.getPlayer().setGlobalX(state.getX());
                state.getPlayer().setGlobalY(state.getY());

                state.getPlayer().setPrevX(state.getX());
                state.getPlayer().setPrevY(state.getY());
                state.getPlayer().setTargetX(state.getX());
                state.getPlayer().setTargetY(state.getY());
                state.getPlayer().setLastUpdateTime(System.currentTimeMillis());
            }

            MoveMessage move = new MoveMessage(id, state.getX(), state.getY());
            server.broadcast(server.json.toJson(move, labelPair(Message.JSON_LABEL, "move")));
        }


        public static void spawnEnemy(Server server, Enemy enemy, int startX, int startY) {

            EnemySpawnMessage spawnMessage = new EnemySpawnMessage(
                    enemy.getId(),
                    enemy.getType(),
                    enemy.getSize(),
                    startX,
                    startY
            );
            server.enemies.put(enemy.getId(), enemy);

            server.broadcast(server.json.toJson(spawnMessage, labelPair(Message.JSON_LABEL, "enemySpawn")));

        }

        public static void spawnPowerUp(Server server, PowerUp powerUp, PowerUpType type, int startX, int startY) {
            long id = powerUp.getId();
            PowerUpSpawnMessage msgPowerUp = new PowerUpSpawnMessage(
                    id,
                    type,
                    startX,
                    startY
            );
            server.powerUps.put(id, powerUp);

            server.broadcast(server.json.toJson(msgPowerUp, labelPair(Message.JSON_LABEL, "powerUpSpawn")));
        }


        public static void broadcastEnemyMove(long enemyId, int x, int y, Server server) {
            EnemyMoveMessage moveMsg = new EnemyMoveMessage(enemyId, x, y);
            server.broadcast(server.json.toJson(moveMsg, labelPair(Message.JSON_LABEL, "enemyMove")));
        }

    }
}
