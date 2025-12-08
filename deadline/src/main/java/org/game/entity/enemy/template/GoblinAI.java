package org.game.entity.enemy.template;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.entity.state.*;

public final class GoblinAI extends EnemyAI {

    private static final double FLEE_HP_THRESHOLD = 0.3;

    @Override
    protected void chooseState(Enemy enemy, Player target) {
        // Followers keep following their leader
        if (enemy.getGroupLeaderRef() != null) {
            return;
        }

        // Check HP first - run away if low
        double hpRatio = (double) enemy.getHitPoints() / enemy.getMaxHitPoints();
        if (hpRatio <= FLEE_HP_THRESHOLD) {
            if (!(enemy.getState() instanceof FleeState)) {
                enemy.getStateContext().setState(FleeState.getInstance());
            }
            return;
        }

        // Otherwise zigzag chase
        if (!(enemy.getState() instanceof ZigZagChaseState)) {
            enemy.getStateContext().setState(ZigZagChaseState.getInstance());
        }
    }

    @Override
    protected void handleOutOfRange(Enemy enemy) {
        // Still run away if HP is low
        double hpRatio = (double) enemy.getHitPoints() / enemy.getMaxHitPoints();
        if (hpRatio <= FLEE_HP_THRESHOLD) {
            if (!(enemy.getState() instanceof FleeState)) {
                enemy.getStateContext().setState(FleeState.getInstance());
            }
            return;
        }

        EnemyState state = enemy.getState();
        if (!(state instanceof IdleState) && !(state instanceof FollowLeaderState)) {
            enemy.getStateContext().setState(IdleState.getInstance());
        }
    }

}