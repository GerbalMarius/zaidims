package org.game.entity.enemy.template;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.entity.state.ChaseState;
import org.game.entity.state.FleeState;
import org.game.entity.state.PatrolState;

public final class SkeletonAI extends EnemyAI {

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
        double hpRatio = (double) enemy.getHitPoints() / enemy.getMaxHitPoints();
        if (hpRatio <= FLEE_HP_THRESHOLD) {
            if (!(enemy.getState() instanceof FleeState)) {
                enemy.getStateContext().setState(FleeState.getInstance());
            }
            return;
        }

        if (!(enemy.getState() instanceof PatrolState)) {
            enemy.getStateContext().setState(enemy.createPatrolState());
        }
    }

    @Override
    protected boolean isInVisionRange(Enemy enemy, double dist) {
        return dist <= 600.0;
    }
}