package org.game.entity.strategy;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.CollisionChecker;

import java.util.Collection;
import java.util.Map;

public interface EnemyStrategy {

    void execute(Enemy enemy, Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionChecker checker);
}
