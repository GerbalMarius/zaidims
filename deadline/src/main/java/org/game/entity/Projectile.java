package org.game.entity;

import lombok.Getter;
import lombok.Setter;
import org.game.server.CollisionChecker;

import java.awt.*;
import java.util.Collection;
import java.util.function.Consumer;

@Getter
@Setter
public final class Projectile extends Entity {

    private int damage;
    private boolean active = true;
    private double maxDistance = Double.POSITIVE_INFINITY;
    private double traveledDistance = 0;

    private final Color color;

    public Projectile(int x, int y, FramePosition direction, int speed, int damage, Color color) {
        super(x, y);
        this.direction = direction;
        this.speed = speed;
        this.damage = damage;
        this.hitbox = new Rectangle(8, 16, 16, 16);
        this.color = color;

    }
    public Projectile(int x, int y, FramePosition direction, int speed, int damage, double maxDistance, Color color) {
        this(x, y, direction, speed, damage, color);
        this.maxDistance = maxDistance;
    }
    public void update(Collection<? extends Enemy> enemies, CollisionChecker checker, Consumer<? super Enemy> healthTickAction) {
        if (!active) return;

        int dx = 0, dy = 0;
        switch (getDirection()) {
            case UP -> dy = -speed;
            case DOWN -> dy = speed;
            case LEFT -> dx = -speed;
            case RIGHT -> dx = speed;
        }

        moveBy(dx, dy);
        traveledDistance += Math.hypot(dx, dy);

        // range check
        if (traveledDistance >= maxDistance) {
            this.active = false;
            return;
        }

        // Tile collision
        checker.checkTile(this);
        if (isCollisionOn()) {
            this.active = false;
            return;
        }

        // Enemy collision
        Rectangle projectileHitbox = new Rectangle(
                getGlobalX() + getHitbox().x,
                getGlobalY() + getHitbox().y,
                getHitbox().width,
                getHitbox().height
        );

        for (Enemy enemy : enemies) {
            Rectangle enemyHitbox = new Rectangle(
                    enemy.getRenderX() + enemy.getHitbox().x,
                    enemy.getRenderY() + enemy.getHitbox().y,
                    enemy.getHitbox().width,
                    enemy.getHitbox().height
            );

            if (projectileHitbox.intersects(enemyHitbox)) {
                enemy.setHitPoints(enemy.getHitPoints() - this.damage);
                this.active = false;

                healthTickAction.accept(enemy);
                break;
            }
        }
    }



    public void draw(Graphics2D g2d) {
        if (!active) return;

        g2d.setColor(this.color);
        g2d.fillOval(getGlobalX(), getGlobalY(), 16, 16);
    }

}
