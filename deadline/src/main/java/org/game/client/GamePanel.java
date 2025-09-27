package org.game.client;

import lombok.Getter;
import lombok.Setter;
import org.game.client.entity.ClassType;
import org.game.client.entity.FramePosition;
import org.game.client.entity.Player;
import org.game.client.tiles.TileManager;
import org.game.message.JoinMessage;
import org.game.message.LeaveMessage;
import org.game.message.Message;
import org.game.message.MoveMessage;
import org.game.server.WorldSettings;
import org.game.utils.Drawer;

import javax.swing.*;
import java.awt.*;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

public final class GamePanel extends JPanel implements Runnable {
    private static final int FPS = 60;

    @Getter
    private final GameState state;

    private final KeyboardHandler keyboardHandler;

    @Getter
    private final UUID clientId;

    private final Camera camera;

    @Setter
    private BiConsumer<Integer, Integer> moveCallback;

    private final Queue<Message> incomingMessages = new ConcurrentLinkedQueue<>();

    private int pendingDx = 0;
    private int pendingDy = 0;
    private long lastSendTime = 0;
    private Thread gameThread;

    @Getter
    private final  TileManager tileManager;
    public CollisionChecker cChecker = new CollisionChecker(this);

    public GamePanel(UUID clientId, GameState state, KeyboardHandler keyboardHandler) {
        this.clientId = clientId;
        this.state = state;
        this.keyboardHandler = keyboardHandler;
        this.camera = new Camera(0.12, 80, 50);
        this.tileManager = new TileManager();


        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this.keyboardHandler);


        initialSnap(state.getPlayer(clientId));
    }

    public void startGameLoop() {
        gameThread = Thread.ofVirtual()
                .name("Game Window")
                .start(this);

    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000D / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                updateGame();
                repaint();
                delta--;
            }
        }
    }

    private void updateGame() {

        Player currentPlayer = state.getPlayer(clientId);
        optimisticMove(currentPlayer);


        long nowMillis = System.currentTimeMillis();
        if (nowMillis - lastSendTime > 50) {
            if (pendingDx != 0 || pendingDy != 0) {
                sendBatchedMove();
                pendingDx = 0;
                pendingDy = 0;
            }
            lastSendTime = nowMillis;
        }
        processNetworkMessages();

        if(currentPlayer != null) {
            currentPlayer.updateCameraPos(this.camera, this.getWidth(), this.getHeight(), WorldSettings.WORLD_WIDTH, WorldSettings.WORLD_HEIGHT);
        }
    }

    private void optimisticMove(Player player) {
        if (player == null || !keyboardHandler.anyKeyPressed()) {
            return;
        }

        int dx = 0, dy = 0;

        int speed = player.getSpeed();

        if (keyboardHandler.isLeftPressed()) dx -= speed;
        if (keyboardHandler.isRightPressed()) dx += speed;
        if (keyboardHandler.isUpPressed()) dy -= speed;
        if (keyboardHandler.isDownPressed()) dy += speed;


        if (dx != 0) {
            player.setCollisionOn(false);
            player.setDirection(dx > 0 ? FramePosition.RIGHT : FramePosition.LEFT);
            cChecker.checkTile(player);
            if (!player.isCollisionOn()) {
                player.moveBy(dx, 0);
                pendingDx += dx;
            }
        }

        if (dy != 0) {
            player.setCollisionOn(false);
            player.setDirection(dy > 0 ? FramePosition.DOWN : FramePosition.UP);
            cChecker.checkTile(player);
            if (!player.isCollisionOn()) {
                player.moveBy(0, dy);
                pendingDy += dy;
            }
        }
    }


    private void sendBatchedMove() {
        if (moveCallback != null) {
            moveCallback.accept(pendingDx, pendingDy);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        double targetX = getWidth() / 2.0 - camera.getX();
        double targetY = getHeight() / 2.0 - camera.getY();
        g2d.translate(targetX, targetY);

        tileManager.draw(g2d);

        for (var playerEntry : state.getPlayerEntries()) {
            Player playerData = playerEntry.getValue();

            playerData.updateDirectionByRender();

            int x, y;
            if (playerEntry.getKey().equals(clientId)) {
                x = playerData.getGlobalX();
                y = playerData.getGlobalY();
            } else {
                x = playerData.getRenderX();
                y = playerData.getRenderY();
            }

            String name = playerData.getName();

            playerData.draw(g2d, x, y, 48);
            Drawer.drawNameBox(g2d, name, x, y, 48);
        }

        g2d.dispose();
    }

    public void processMessage(final Message message) {
        incomingMessages.offer(message);
    }

    private void processNetworkMessages() {
        Message msg;
        int processed = 0;
        int maxMsgPerTick = 100;
        while (processed < maxMsgPerTick && (msg = incomingMessages.poll()) != null) {
            switch (msg) {
                case JoinMessage(UUID playerId, ClassType playerClass, String name, int x, int y) -> state.addPlayer(playerId, playerClass, name, x, y);
                case LeaveMessage(UUID playerId) -> state.removePlayer(playerId);
                case MoveMessage(UUID playerId, int x, int y) -> {
                    if (!playerId.equals(clientId)) {
                        Player player = state.getPlayer(playerId);
                        if (player != null) {
                            player.updateFromServer(x, y);
                        }
                    }
                }
            }
            processed++;
        }
    }

    private void initialSnap(Player player) {
        if (player == null) {
            return;
        }
        this.camera.snapTo(player.getGlobalX(), player.getGlobalY(), getHeight(), getWidth(), WorldSettings.WORLD_WIDTH, WorldSettings.WORLD_HEIGHT);
    }
}
