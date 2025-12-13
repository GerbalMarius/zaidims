package org.game.entity.enemy.template;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.entity.state.ChaseState;
import org.game.entity.state.FleeState;
import org.game.entity.state.IdleState;

public final class ZombieAI extends EnemyAI {

    private static final double FLEE_HP_THRESHOLD = 0.2;

    @Override
    protected void chooseState(Enemy enemy, Player target) {
        // Check HP first - flee if low
        double hpRatio = (double) enemy.getHitPoints() / enemy.getMaxHitPoints();
        if (hpRatio <= FLEE_HP_THRESHOLD) {
            if (!(enemy.getState() instanceof FleeState)) {
                enemy.getStateContext().setState(FleeState.getInstance());
            }
            return;
        }

        // Otherwise chase
        if (!(enemy.getState() instanceof ChaseState)) {
            enemy.getStateContext().setState(ChaseState.getInstance());
        }
    }

    @Override
    protected void handleOutOfRange(Enemy enemy) {
        // Still flee if HP is low, even out of range
        double hpRatio = (double) enemy.getHitPoints() / enemy.getMaxHitPoints();
        if (hpRatio <= FLEE_HP_THRESHOLD) {
            if (!(enemy.getState() instanceof FleeState)) {
                enemy.getStateContext().setState(FleeState.getInstance());
            }
            return;
        }

        if (!(enemy.getState() instanceof IdleState)) {
            enemy.getStateContext().setState(IdleState.getInstance());
        }
    }

}
