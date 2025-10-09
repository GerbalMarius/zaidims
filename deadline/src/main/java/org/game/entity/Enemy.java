package org.game.entity;

import lombok.Getter;
import lombok.Setter;
import org.game.entity.strategy.ChaseStrategy;
import org.game.entity.strategy.EnemyStrategy;
import org.game.entity.strategy.WanderStrategy;
import org.game.server.CollisionChecker;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public abstract non-sealed class Enemy extends Entity {

    protected EnemyType  type;
    protected EnemySize  size;

    protected EnemyStrategy strategy;


    protected Enemy(int x, int y) {
        super(x, y);
        configureStats();

    }


    protected abstract void configureStats();

    protected  void createHitbox() {
        this.hitbox = new Rectangle(8, 16, 11*scale, 11*scale);
    }

    public void updateAI(Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        Player target = getClosestPlayer(players);
        if (target == null) return;

        double dx = target.getGlobalX() - this.getGlobalX();
        double dy = target.getGlobalY() - this.getGlobalY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        double visionRange = 500.0;
        double loseSightRange = 350.0;

        if (strategy == null) {
            strategy = new WanderStrategy();
        }

        if (strategy instanceof WanderStrategy && distance <= visionRange) {
            strategy = new ChaseStrategy();
        } else if (strategy instanceof ChaseStrategy && distance > loseSightRange) {
            strategy = new WanderStrategy();
        }

        strategy.execute(this, players, allEnemies, checker);

    }

    public void tryMove(int mx, int my, Collection<Enemy> otherEnemies, CollisionChecker checker) {
        int steps = Math.max(Math.abs(mx), Math.abs(my));
        if (steps == 0) return;

        int stepX = mx / steps;
        int stepY = my / steps;

        for (int i = 0; i < steps; i++) {
            setGlobalX(getGlobalX() + stepX);
            setGlobalY(getGlobalY() + stepY);
            setCollisionOn(false);

            // tikriname plyteles
            checker.checkTile(this);

            // tikriname kitus priees
            checker.checkEntityCollision(this, otherEnemies);
            //checker.checkEntityCollision(this, players);

            if (isCollisionOn()) {
                setGlobalX(getGlobalX() - stepX);
                setGlobalY(getGlobalY() - stepY);
                return;
            }

            moveBy(stepX, stepY);
        }
    }

    public Player getClosestPlayer(Collection<Player> players) {
        Player closest = null;
        double minDistanceSq = Double.MAX_VALUE;

        for (Player p : players) {
            double dx = p.getGlobalX() - this.getGlobalX();
            double dy = p.getGlobalY() - this.getGlobalY();
            double distSq = dx * dx + dy * dy;

            if (distSq < minDistanceSq) {
                minDistanceSq = distSq;
                closest = p;
            }
        }

        return closest;
    }

}
