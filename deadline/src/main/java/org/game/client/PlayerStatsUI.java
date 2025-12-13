package org.game.client;

import org.game.entity.Player;

import java.awt.*;

public final class PlayerStatsUI {

    private static final int PADDING = 12;
    private static final int WIDTH = 220;
    private static final int HEIGHT = 120;

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

        int textX = x + 12;
        int textY = y + 20;

        // ---- Name & class ----
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString(player.getName(), textX, textY);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
        g2.drawString(
                player.getPlayerClass().name(),
                textX,
                textY + 14
        );

        // ---- HP bar ----
        drawBar(
                g2,
                textX,
                textY + 26,
                180,
                10,
                player.getHitPoints(),
                player.getMaxHitPoints(),
                Color.GREEN
        );

        // ---- Stats ----
        int statsY = textY + 52;
        g2.drawString("ATK: " + player.getAttack(), textX, statsY);
        g2.drawString("SPD: " + player.getSpeed(), textX + 90, statsY);

        // ---- Armor ----
        g2.drawString("Armor: " + player.getArmorCount(), textX, statsY + 16);

        // ---- Shield ----
        if (player.isShieldActive()) {
            g2.setColor(new Color(135, 206, 250));
            g2.drawString("SHIELD ACTIVE", textX, statsY + 32);
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

        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(x, y, width, height, 6, 6);

        g2.setColor(color);
        g2.fillRoundRect(x, y, filled, height, 6, 6);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, width, height, 6, 6);
    }
}
