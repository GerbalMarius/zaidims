package org.game.entity.strategy;

import org.game.entity.Enemy;
import org.game.entity.FramePosition;
import org.game.entity.Player;
import org.game.server.CollisionCheckerServer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ChaseStrategy implements EnemyStrategy {


    @Override
    public void execute(Enemy enemy, Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionCheckerServer checker) {
        Player target = enemy.getClosestPlayer(players);
        if (target == null) return;

        int dx = Integer.compare(target.getGlobalX(), enemy.getGlobalX());
        int dy = Integer.compare(target.getGlobalY(), enemy.getGlobalY());

        List<Enemy> otherEnemies = allEnemies.values()
                .stream()
                .filter(e -> e != enemy)
                .toList();

        if (dx != 0) {
            enemy.setDirection(dx > 0 ? FramePosition.RIGHT : FramePosition.LEFT);
            enemy.tryMove(dx * enemy.getSpeed(), 0, otherEnemies, checker);

        }

        if (dy != 0) {
            enemy.setDirection(dy > 0 ? FramePosition.DOWN : FramePosition.UP);
            enemy.tryMove(0, dy * enemy.getSpeed(), otherEnemies, checker);
        }
    }
}
