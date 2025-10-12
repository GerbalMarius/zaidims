package org.game.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public final class Projectile extends Entity {

    private int damage;
    private boolean active = true;

    public Projectile(int x, int y, FramePosition dir, int speed, int damage) {
        super(x, y);
        setDirection(dir);
        setSpeed(speed);
        this.damage = damage;
        this.hitbox = new Rectangle(8, 8, 8, 8);
    }

    public void update() {
        if (!active) return;

        switch(getDirection()) {
            case UP -> moveBy(0, -getSpeed());
            case DOWN -> moveBy(0, getSpeed());
            case LEFT -> moveBy(-getSpeed(), 0);
            case RIGHT -> moveBy(getSpeed(), 0);
        }
    }

    public void draw(Graphics2D g2d) {
        if (!active) return;

        g2d.setColor(Color.ORANGE);
        g2d.fillOval(getGlobalX(), getGlobalY(), 10, 10);
    }

}
