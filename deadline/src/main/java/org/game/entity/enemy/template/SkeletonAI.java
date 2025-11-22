package org.game.entity.enemy.template;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.entity.strategy.ChaseStrategy;
import org.game.entity.strategy.PatrolStrategy;

public final class SkeletonAI extends EnemyAI {

    @Override
    protected void chooseStrategy(Enemy enemy, Player target) {
        if (!(enemy.getStrategy() instanceof ChaseStrategy)) {
            enemy.setStrategy(new ChaseStrategy());
        }
    }

    @Override
    protected void handleOutOfRange(Enemy enemy) {
        if (!(enemy.getStrategy() instanceof PatrolStrategy)) {
            enemy.setStrategy(enemy.enablePatrolStrategy());
        }
    }

    @Override
    protected boolean isInVisionRange(Enemy enemy, double dist) {
        return dist <= 650.0;
    }

}
