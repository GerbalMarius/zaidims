package org.game.entity.strategy;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.CollisionChecker;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ZigZagChaseStrategy implements EnemyStrategy {

    private int zigzagStep = 8;
    private boolean zigRight = true;

    @Override
    public void execute(Enemy enemy, Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        Player target = enemy.getClosestPlayer(players);
        if (target == null) return;

        int dx = Integer.compare(target.getGlobalX(), enemy.getGlobalX());
        int dy = Integer.compare(target.getGlobalY(), enemy.getGlobalY());

        // Zigzag logic
        int moveX = dx;

        int zigzagWidth = 8;
        if (zigzagStep < zigzagWidth) {
            moveX += zigRight ? 1 : -1;
            zigzagStep++;
        } else {
            zigzagStep = 0;
            zigRight = !zigRight;
        }

        List<Enemy> others = allEnemies.values().stream()
                .filter(e -> e != enemy)
                .toList();

        enemy.tryMove(moveX * enemy.getSpeed(), dy * enemy.getSpeed(), others, checker);
    }
}
