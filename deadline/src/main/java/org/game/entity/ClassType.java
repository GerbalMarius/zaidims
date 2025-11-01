package org.game.entity;

import javax.swing.*;
import java.awt.*;

public enum ClassType {
    WARRIOR, WIZARD, ROGUE;

    public String getClassPrefix() {
       return switch (this) {
            case WIZARD -> "wiz";
            case ROGUE -> "rog";
            case WARRIOR -> "war";
        };
    }

    public Color attackColor() {
        return switch (this) {
            case WARRIOR -> Color.WHITE;
            case WIZARD -> Color.ORANGE;
            case ROGUE -> Color.MAGENTA;
        };
    }

    public ImageIcon getIcon() {
       return switch (this) {
            case WARRIOR -> scaleIcon(new ImageIcon("assets/player/war_down_1.png"));
            case ROGUE -> scaleIcon(new ImageIcon("assets/player/rog_down_1.png"));
            case WIZARD ->  scaleIcon(new ImageIcon("assets/player/wiz_down_1.png"));
        };
    }

    @Override
    public String toString() {
        String name = this.name();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    private ImageIcon scaleIcon(ImageIcon icon) {
        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
