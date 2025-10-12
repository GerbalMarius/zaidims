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

    public Projectile(int x, int y, FramePosition direction, int speed, int damage) {
        super(x, y);
        this.direction = direction;
        this.speed = speed;
        this.damage = damage;
        this.hitbox = new Rectangle(8, 16, 16, 16);
    }

    public void update(Collection<? extends Enemy> enemies, CollisionChecker checker, Consumer<? super Enemy> healthTickAction) {
        if (!active) return;

        switch (getDirection()) {
            case UP -> moveBy(0, -speed);
            case DOWN -> moveBy(0, speed);
            case LEFT -> moveBy(-speed, 0);
            case RIGHT -> moveBy(speed, 0);
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

        g2d.setColor(Color.ORANGE);
        g2d.fillOval(getGlobalX(), getGlobalY(), 16, 16);
    }

}
