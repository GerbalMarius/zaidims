package org.game.entity.state;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.CollisionChecker;
import org.game.server.Server;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public interface EnemyState {
    void update(EnemyStateContext context, Enemy enemy, Collection<Player> players,
                Map<Long, Enemy> allEnemies, CollisionChecker checker, Server server);

    String getStateName();

    default Set<Class<? extends EnemyState>> getAllowedTransitions() {
        return Collections.emptySet();
    }
}