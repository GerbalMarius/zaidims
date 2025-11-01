package org.game.entity;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class GlobalUI {
    private static final GlobalUI INSTANCE = new GlobalUI();

    private final AtomicInteger currentEnemyCount = new AtomicInteger(0);

    private GlobalUI() {

    }

    public static GlobalUI getInstance() {
        return INSTANCE;
    }

    public void incrementCounter() {
        currentEnemyCount.incrementAndGet();
    }

    public void decrementCounter() {
        currentEnemyCount.decrementAndGet();
    }

    public void drawCounter(Graphics2D g2d, int panelWidth) {
        String counterText = "Enemies Remaining: " + currentEnemyCount.get();
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(counterText);
        int padding = 10;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(panelWidth - textWidth - (padding * 2), padding,
                textWidth + (padding * 2), fontMetrics.getHeight() + padding, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.drawString(counterText, panelWidth - textWidth - padding,
                fontMetrics.getHeight() + padding / 2);
    }
}
