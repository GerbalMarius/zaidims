package org.game.client;

import org.game.entity.Player;

import java.awt.*;

public final class PlayerStatsUI {

    private static final int PADDING = 12;
    private static final int WIDTH = 260;
    private static final int HEIGHT = 140;

    private PlayerStatsUI() {}

    public static void draw(Graphics2D g2, Player player) {
        if (player == null) return;

        int x = PADDING;
        int y = PADDING;

        // ---- Background ----
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(x, y, WIDTH, HEIGHT, 16, 16);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, WIDTH, HEIGHT, 16, 16);

        int textX = x + 14;
        int textY = y + 26;

        // ---- Name ----
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        g2.drawString(player.getName(), textX, textY);

        // ---- Class ----
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 13f));
        g2.drawString(
                player.getPlayerClass().name(),
                textX,
                textY + 16
        );

        // ---- HP BAR ----
        int barX = textX;
        int barY = textY + 36;
        int barWidth = 210;
        int barHeight = 14;

        drawBar(
                g2,
                barX,
                barY,
                barWidth,
                barHeight,
                player.getHitPoints(),
                player.getMaxHitPoints(),
                new Color(200, 40, 40)
        );

        // ---- HP TEXT CENTERED ----
        String hpText = player.getHitPoints() + " / " + player.getMaxHitPoints();
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        FontMetrics fm = g2.getFontMetrics();

        int hpTextX = barX + (barWidth - fm.stringWidth(hpText)) / 2;
        int hpTextY = barY + barHeight - 3;

        g2.setColor(Color.WHITE);
        g2.drawString(hpText, hpTextX, hpTextY);

        // ---- STATS ----
        int statsY = barY + 30;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13f));
        g2.drawString("ATK: " + player.getAttack(), textX, statsY);
        g2.drawString("SPD: " + player.getSpeed(), textX + 110, statsY);

        g2.drawString("ARMOR: " + player.getArmorCount(), textX, statsY + 18);

        // ---- SHIELD ----
        if (player.isShieldActive()) {
            g2.setColor(new Color(135, 206, 250));
            g2.drawString("SHIELD ACTIVE", textX, statsY + 36);
            g2.setColor(Color.WHITE);
        }
    }

    private static void drawBar(
            Graphics2D g2,
            int x,
            int y,
            int width,
            int height,
            int value,
            int max,
            Color color
    ) {
        if (max <= 0) return;

        double ratio = Math.max(0, Math.min(1, (double) value / max));
        int filled = (int) (width * ratio);

        // Background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(x, y, width, height, 10, 10);

        // Fill
        g2.setColor(color);
        g2.fillRoundRect(x, y, filled, height, 10, 10);

        // Border
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, width, height, 10, 10);
    }
}
