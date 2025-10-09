package org.game.entity.strategy;

import org.game.entity.Enemy;
import org.game.entity.FramePosition;
import org.game.entity.Player;
import org.game.server.CollisionCheckerServer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WanderStrategy implements EnemyStrategy {

    private final Random random = new Random();
    private int wanderTimer = 0;
    private FramePosition randomDir = FramePosition.DOWN;

    @Override
    public void execute(Enemy enemy, Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionCheckerServer checker) {
        if (wanderTimer <= 0) {
            FramePosition[] dirs = FramePosition.values();
            randomDir = dirs[random.nextInt(dirs.length)];
            wanderTimer = 40 + random.nextInt(60);
        } else {
            wanderTimer--;
        }

        int dx = 0, dy = 0;
        switch (randomDir) {
            case LEFT -> dx = -1;
            case RIGHT -> dx = 1;
            case UP -> dy = -1;
            case DOWN -> dy = 1;
        }

        List<Enemy> others = allEnemies.values()
                .stream()
                .filter(e -> e != enemy)
                .toList();

        enemy.tryMove(dx * enemy.getSpeed(), dy * enemy.getSpeed(), others, checker);
    }
}
