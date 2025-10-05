package org.game.entity;

import java.awt.*;

public class GlobalUI {
    private static final GlobalUI INSTANCE = new GlobalUI();

    private int count = 0;

    private GlobalUI() {

    }

    public static GlobalUI getInstance() {
        return INSTANCE;
    }

    public synchronized void incrementCounter() {
        count++;
    }

    public synchronized void decrementCounter() {
        if (count > 0) {
            count--;
        }
    }

    public synchronized void reset() {
        count = 0;
    }

    public synchronized int getCount() {
        return count;
    }

    public void drawCounter(Graphics2D g2d, int panelWidth) {
        String counterText = "Enemies Remaining: " + count;
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
