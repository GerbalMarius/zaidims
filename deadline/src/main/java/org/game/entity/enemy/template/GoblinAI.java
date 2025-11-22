package org.game.entity.enemy.template;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.entity.strategy.RunAwayStrategy;
import org.game.entity.strategy.WanderStrategy;
import org.game.entity.strategy.ZigZagChaseStrategy;

public final class GoblinAI extends EnemyAI {

    @Override
    protected void chooseStrategy(Enemy enemy, Player target) {
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
        if (!(enemy.getStrategy() instanceof WanderStrategy)) {
            enemy.setStrategy(new WanderStrategy());
        }
    }

}