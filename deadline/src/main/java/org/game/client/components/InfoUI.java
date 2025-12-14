package org.game.client.components;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.entity.iterator.EnemyIterator;
import org.game.entity.iterator.PlayerIterator;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InfoUI {
    private boolean infoOpen = false;
    private static final int PADDING = 20;
    private static final int LINE_HEIGHT = 25;
    private static final int PANEL_WIDTH = 400;

    public void toggleInfo() {
        infoOpen = !infoOpen;
    }

    public boolean isInfoOpen() {
        return infoOpen;
    }

    public void render(Graphics2D g2, int screenWidth, int screenHeight,
                       Map<UUID, Player> players, Map<Long, Enemy> enemies) {
        if (!infoOpen) return;

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();

        int startX = (screenWidth - PANEL_WIDTH) / 2;
        int startY = 50;
        int currentY = startY;

        // Draw background panel
        g2.setColor(new Color(0, 0, 0, 200));
        int panelHeight = calculatePanelHeight(players, enemies);
        g2.fillRoundRect(startX, startY, PANEL_WIDTH, panelHeight, 15, 15);

        // Draw border
        g2.setColor(Color.CYAN);
        g2.drawRoundRect(startX, startY, PANEL_WIDTH, panelHeight, 15, 15);

        currentY += PADDING;

        // Draw title
        g2.setColor(Color.WHITE);
        String title = "Game Info (Press I to close)";
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, startX + (PANEL_WIDTH - titleWidth) / 2, currentY);
        currentY += LINE_HEIGHT + 10;

        // Draw separator
        g2.setColor(Color.GRAY);
        g2.drawLine(startX + PADDING, currentY, startX + PANEL_WIDTH - PADDING, currentY);
        currentY += 15;

        // Draw Players section
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.GREEN);
        g2.drawString("Players:", startX + PADDING, currentY);
        currentY += LINE_HEIGHT;

        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        PlayerIterator playerIterator = new PlayerIterator(players);
        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            String playerInfo = String.format("  %s - HP: %d/%d",
                    player.getName(),
                    player.getHitPoints(),
                    player.getMaxHitPoints());

            g2.setColor(Color.WHITE);
            g2.drawString(playerInfo, startX + PADDING, currentY);

            // Draw health bar
            int barX = startX + PADDING + 200;
            int barY = currentY - 10;
            int barWidth = 150;
            int barHeight = 12;

            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(barX, barY, barWidth, barHeight);

            float healthPercent = (float) player.getHitPoints() / player.getMaxHitPoints();
            g2.setColor(player.getHitPoints() > player.getMaxHitPoints() * 0.5 ? Color.GREEN : Color.RED);
            g2.fillRect(barX, barY, (int) (barWidth * healthPercent), barHeight);

            g2.setColor(Color.WHITE);
            g2.drawRect(barX, barY, barWidth, barHeight);

            currentY += LINE_HEIGHT;
        }

        currentY += 10;

        // Draw Enemies section
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.RED);
        g2.drawString("Enemies:", startX + PADDING, currentY);
        currentY += LINE_HEIGHT;

        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(Color.WHITE);

        // Count enemies by type
        Map<String, Integer> enemyCounts = new HashMap<>();
        EnemyIterator enemyIterator = new EnemyIterator(enemies);
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            String enemyType = enemy.getClass().getSimpleName();
            enemyCounts.put(enemyType, enemyCounts.getOrDefault(enemyType, 0) + 1);
        }

        if (enemyCounts.isEmpty()) {
            g2.drawString("  No enemies present", startX + PADDING, currentY);
            currentY += LINE_HEIGHT;
        } else {
            for (Map.Entry<String, Integer> entry : enemyCounts.entrySet()) {
                String enemyInfo = String.format("  %s: %d", entry.getKey(), entry.getValue());
                g2.drawString(enemyInfo, startX + PADDING, currentY);
                currentY += LINE_HEIGHT;
            }
        }

        currentY += 10;
        g2.setColor(Color.GRAY);
        String totalEnemies = "Total Enemies: " + enemies.size();
        g2.drawString(totalEnemies, startX + PADDING, currentY);
    }

    private int calculatePanelHeight(Map<UUID, Player> players, Map<Long, Enemy> enemies) {
        int baseHeight = 100;
        int playerLines = players.size();
        int enemyTypeLines = countEnemyTypes(enemies) + 1; // +1 for total count

        return baseHeight + (playerLines * LINE_HEIGHT) + (enemyTypeLines * LINE_HEIGHT);
    }

    private int countEnemyTypes(Map<Long, Enemy> enemies) {
        Map<String, Integer> types = new HashMap<>();
        EnemyIterator iterator = new EnemyIterator(enemies);
        while (iterator.hasNext()) {
            types.put(iterator.next().getClass().getSimpleName(), 1);
        }
        return Math.max(1, types.size());
    }
}
