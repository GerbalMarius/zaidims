package org.game.entity.strategy;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.CollisionChecker;

import java.util.Collection;
import java.util.Map;


public class RunAwayStrategy implements EnemyStrategy {

    @Override
    public void execute(Enemy enemy, Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        Player threat = enemy.getClosestPlayer(players);
        if (threat == null) return;

        int dx = Integer.compare(enemy.getGlobalX(), threat.getGlobalX());
        int dy = Integer.compare(enemy.getGlobalY(), threat.getGlobalY());

        var others = allEnemies.values().stream()
                .filter(e -> e != enemy)
                .toList();

        enemy.tryMove(dx * enemy.getSpeed(), dy * enemy.getSpeed(), others, checker);
    }
}
