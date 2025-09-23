package org.game.client;

import lombok.Setter;
import org.game.client.entity.Player;
import org.game.message.JoinMessage;
import org.game.message.LeaveMessage;
import org.game.message.Message;
import org.game.message.MoveMessage;

import javax.swing.*;
import java.awt.*;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

public class GamePanel extends JPanel implements Runnable {
    private static final int FPS = 60;
    private final GameState state;
    private final KeyboardHandler keyboardHandler;
    private final UUID clientId;
    private final Camera camera;

    private final int worldWidth;
    private final int worldHeight;

    @Setter
    private BiConsumer<Integer, Integer> moveCallback;

    private final Queue<Message> incomingMessages = new ConcurrentLinkedQueue<>();

    private int pendingDx = 0;
    private int pendingDy = 0;
    private long lastSendTime = 0;
    private Thread gameThread;

    public GamePanel(UUID clientId, GameState state, KeyboardHandler keyboardHandler, int worldWidth, int worldHeight) {
        this.clientId = clientId;
        this.state = state;
        this.keyboardHandler = keyboardHandler;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.camera = new Camera(0.12, 80, 50);


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
            currentPlayer.updateCameraPos(this.camera, getWidth(), getHeight(), worldWidth, worldHeight);
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

        if (dx != 0 || dy != 0) {
            pendingDx += dx;
            pendingDy += dy;
            player.moveBy(dx, dy);
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

        double camX = camera.getX();
        double camY = camera.getY();
        double targetX = getWidth() / 2.0 - camX;
        double targetY = getHeight() / 2.0 - camY;
        g2d.translate(targetX, targetY);

        g2d.setColor(Color.BLUE);
        int radius = 10;

        for (var playerEntry : state.getPlayerEntries()) {
            Player playerData = playerEntry.getValue();

            int x, y;
            if (playerEntry.getKey().equals(clientId)) {
                x = playerData.getGlobalX();
                y = playerData.getGlobalY();
            } else {
                x = playerData.getRenderX();
                y = playerData.getRenderY();
            }

            String name = playerData.getName();

            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            g2d.drawString(name, x + radius + 2, y);
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
                case JoinMessage(UUID playerId, String name, int x, int y) -> state.addPlayer(playerId, name, x, y);
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
        this.camera.snapTo(player.getGlobalX(), player.getGlobalY(), getWidth(), getHeight(), worldWidth, worldHeight);
    }
}
