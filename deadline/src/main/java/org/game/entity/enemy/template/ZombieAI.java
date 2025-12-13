package org.game.entity.enemy.template;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.entity.state.ChaseState;
import org.game.entity.state.IdleState;

public final class ZombieAI extends EnemyAI {

    @Override
    protected void chooseState(Enemy enemy, Player target) {

        if (!(enemy.getState() instanceof ChaseState)) {
            enemy.getStateContext().setState(ChaseState.getInstance());
        }
    }

    @Override
    protected void handleOutOfRange(Enemy enemy) {

        if (!(enemy.getState() instanceof IdleState)) {
            enemy.getStateContext().setState(IdleState.getInstance());
        }
    }

}
