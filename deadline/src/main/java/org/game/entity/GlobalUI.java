package org.game.entity;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class GlobalUI {
    private static final GlobalUI INSTANCE = new GlobalUI();

    private static final int PADDING = 12;
    private static final int CORNER_RADIUS = 14;

    private final AtomicInteger currentEnemyCount = new AtomicInteger(0);

    private GlobalUI() {}

    public static GlobalUI getInstance() {
        return INSTANCE;
    }

    public void incrementCounter() {
        currentEnemyCount.incrementAndGet();
    }

    public void decrementCounter() {
        currentEnemyCount.decrementAndGet();
    }

    public void drawCounter(Graphics2D g2, int panelWidth) {
        String text = "Enemies: " + currentEnemyCount.get();

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        FontMetrics fm = g2.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int boxWidth = textWidth + PADDING * 2;
        int boxHeight = textHeight + PADDING;

        int x = panelWidth - boxWidth - PADDING;
        int y = PADDING;

        // ---- Background ----
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(x, y, boxWidth, boxHeight, CORNER_RADIUS, CORNER_RADIUS);

        // ---- White border (like Player UI) ----
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, boxWidth, boxHeight, CORNER_RADIUS, CORNER_RADIUS);

        // ---- Text ----
        int textX = x + PADDING;
        int textY = y + ((boxHeight - fm.getHeight()) / 2) + fm.getAscent();

        g2.drawString(text, textX, textY);
    }
}
