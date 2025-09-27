package org.game.utils;

import java.awt.*;

public final class Drawer {
    private Drawer(){}

    public static void drawNameBox(Graphics2D g2d, String name, int x, int y, int tileSize) {

        Object aaHint = g2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        Font original = g2d.getFont();

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font font = new Font("Cascadia Code", Font.BOLD, 12);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics(font);

        int padding = 6;
        int textW = fm.stringWidth(name);
        int textH = fm.getHeight();

        int boxW = textW + padding * 2;
        int boxH = textH + padding;


        int bx = x + (tileSize - boxW) / 2;
        int by = y - boxH - 10;


        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(bx, by, boxW, boxH);


        int textX = bx + padding;
        int textY = by + (boxH + fm.getAscent() - fm.getDescent()) / 2;
        g2d.setColor(new Color(0,0,0,180));
        g2d.drawString(name, textX + 1, textY + 1);


        g2d.setColor(Color.WHITE);
        g2d.drawString(name, textX, textY);


        g2d.setFont(original);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
    }

}
