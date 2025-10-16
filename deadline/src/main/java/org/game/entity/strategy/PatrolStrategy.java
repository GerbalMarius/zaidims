package org.game.entity.strategy;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.CollisionChecker;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PatrolStrategy implements EnemyStrategy {

    private int pointIndex = 0;
    private boolean forward = true;
    private final int[][] patrolPoints;

    public PatrolStrategy(int[][] patrolPoints) {
        this.patrolPoints = patrolPoints;
    }

    @Override
    public void execute(Enemy enemy, Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionChecker checker) {

        if (patrolPoints.length == 0) return;

        int targetX = patrolPoints[pointIndex][0];
        int targetY = patrolPoints[pointIndex][1];

        int dx = Integer.compare(targetX, enemy.getGlobalX());
        int dy = Integer.compare(targetY, enemy.getGlobalY());

        List<Enemy> others = allEnemies.values().stream()
                .filter(e -> e != enemy)
                .toList();

        enemy.tryMove(dx * enemy.getSpeed(), dy * enemy.getSpeed(), others, checker);


        double dist = Math.hypot(targetX - enemy.getGlobalX(), targetY - enemy.getGlobalY());

        if (dist < 10) {
            if (forward) {
                pointIndex++;
                if (pointIndex >= patrolPoints.length - 1) {
                    forward = false;
                }
            } else {
                pointIndex--;
                if (pointIndex <= 0) {
                    forward = true;
                }
            }
        }
    }
}
