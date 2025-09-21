package org.game.client;

import lombok.Setter;
import org.game.client.entity.Player;
import org.game.message.JoinMessage;
import org.game.message.LeaveMessage;
import org.game.message.Message;
import org.game.message.MoveMessage;

import javax.swing.*;
import java.awt.*;

import java.util.UUID;
import java.util.function.BiConsumer;

public class GamePanel extends JPanel implements Runnable {
    private static final int FPS = 60;
    private final GameState state;
    private final KeyboardHandler keyboardHandler;
    private final UUID clientId;

    @Setter
    private BiConsumer<Integer, Integer> sendMoveCallback;

    private int pendingDx = 0;
    private int pendingDy = 0;
    private long lastSendTime = 0;
    private Thread gameThread;

    public GamePanel(UUID clientId, GameState state, KeyboardHandler keyboardHandler) {
        this.clientId = clientId;
        this.state = state;
        this.keyboardHandler = keyboardHandler;
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(keyboardHandler);
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
        int dx = 0, dy = 0;
        if (keyboardHandler.isLeftPressed()) dx -= 5;
        if (keyboardHandler.isRightPressed()) dx += 5;
        if (keyboardHandler.isUpPressed()) dy -= 5;
        if (keyboardHandler.isDownPressed()) dy += 5;

        if (dx != 0 || dy != 0) {
            pendingDx += dx;
            pendingDy += dy;
            optimisticMove(dx, dy);
        }


        long nowMillis = System.currentTimeMillis();
        if (nowMillis - lastSendTime > 100) {
            if (pendingDx != 0 || pendingDy != 0) {
                sendBatchedMove();
                pendingDx = 0;
                pendingDy = 0;
            }
            lastSendTime = nowMillis;
        }
    }

    private void optimisticMove(int dx, int dy) {
        Player local = state.getPlayer(clientId);
        if (local != null) {
            local.moveBy(dx, dy);
        }
    }


    private void sendBatchedMove() {
        if (sendMoveCallback != null) {
            sendMoveCallback.accept(pendingDx, pendingDy);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        int radius = 10;
        for (var entry : state.getPlayerEntries()) {
            Player playerData = entry.getValue();

            int x = playerData.getX();
            int y = playerData.getY();
            String name = playerData.getName();


            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            g2d.drawString(name, x + radius + 2, y);
        }
    }

    public void processMessage(final Message message) {
        SwingUtilities.invokeLater(() -> {
            switch (message) {
                case JoinMessage(UUID playerId, String name, int x, int y) -> state.addPlayer(playerId, name, x, y);
                case LeaveMessage(UUID playerId)  -> state.removePlayer(playerId);
                case MoveMessage(UUID playerId, int x, int y) -> {
                    Player player = state.getPlayer(playerId);
                    if (player != null && !playerId.equals(clientId)) {
                        player.updateFromServer(x, y);
                    }
                }

            }
            this.repaint();
        });
    }
}
