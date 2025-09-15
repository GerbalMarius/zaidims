package org.game.client;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final GameState state;

    public GamePanel(GameState state) {
        this.state = state;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        int radius = 10;
        for (var player : state.getPlayerViews().entrySet()) {
            PlayerInfo info = player.getValue();
            Point coordinates = info.coordinates();

            g2d.fillOval(coordinates.x - radius, coordinates.y - radius, radius * 2, radius * 2);
            g2d.drawString(info.name(), coordinates.x + radius + 2, coordinates.y);
        }
    }
}
