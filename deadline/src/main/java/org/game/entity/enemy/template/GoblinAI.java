package org.game.entity.enemy.template;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.entity.strategy.*;

public final class GoblinAI extends EnemyAI {

    @Override
    protected void chooseStrategy(Enemy enemy, Player target) {
        if (enemy.getGroupLeaderRef() != null) {
            return;
        }

        double lowHpThreshold = enemy.getMaxHitPoints() * 0.3;

        if (enemy.getHitPoints() <= lowHpThreshold) {
            if (!(enemy.getStrategy() instanceof RunAwayStrategy)) {
                enemy.setStrategy(new RunAwayStrategy());
            }
            return;
        }

        if (!(enemy.getStrategy() instanceof ZigZagChaseStrategy)) {
            enemy.setStrategy(new ZigZagChaseStrategy());
        }
    }

    @Override
    protected void handleOutOfRange(Enemy enemy) {
        EnemyStrategy strategy = enemy.getStrategy();
        if (!(strategy instanceof WanderStrategy) && !(strategy instanceof FollowLeaderStrategy)) {
            enemy.setStrategy(new WanderStrategy());
        }
    }

}