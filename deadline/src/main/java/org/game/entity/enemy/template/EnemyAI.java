package org.game.entity.enemy.template;

import lombok.extern.slf4j.Slf4j;
import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.CollisionChecker;
import org.game.server.Server;

import java.util.Collection;
import java.util.Map;

@Slf4j
public abstract class EnemyAI {

    public final void updateAI( Enemy enemy,
                            Collection<Player> players,
                            Map<Long, Enemy> enemies,
                            CollisionChecker checker,
                            Server server) {

        Player target = enemy.getClosestPlayer(players);
        if (target == null) return;

        double distance = enemy.calculateDistanceTo(target);

        if (isInVisionRange(enemy, distance)) {
            chooseStrategy(enemy, target);
            tryAttack(enemy, target, server);
        } else {
            handleOutOfRange(enemy);
        }

        executeStrategy(enemy, players, enemies, checker);
    }

    // Hooks – overridable
    protected boolean isInVisionRange(Enemy enemy, double dist) {
        return dist <= 500.0;
    }

    protected void tryAttack(Enemy enemy, Player target, Server server) {
        enemy.tryAttack(target, server);
    }

    protected void executeStrategy(Enemy enemy, Collection<Player> players, Map<Long, Enemy> enemies, CollisionChecker checker) {
        if(enemy.getStrategy() != null) {
            enemy.getStrategy().execute(enemy, players, enemies, checker);
        }
    }

    // override konkreciuose ai
    protected abstract void chooseStrategy(Enemy enemy, Player target);

    protected abstract void handleOutOfRange(Enemy enemy);

}
