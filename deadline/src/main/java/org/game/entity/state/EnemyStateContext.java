package org.game.entity.state;

import lombok.Getter;
import lombok.Setter;
import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.CollisionChecker;
import org.game.server.Server;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class EnemyStateContext {
    private EnemyState currentState;
    private long stateEnteredTime;
    private Player lastKnownTarget;

    private static final double CHASE_RANGE = 400.0;
    private static final double ATTACK_RANGE = 50.0;
    private static final double FLEE_HP_THRESHOLD = 0.2;

    public EnemyStateContext() {
        this.currentState = IdleState.getInstance();
        this.stateEnteredTime = System.currentTimeMillis();
    }

    public void setState(EnemyState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            this.stateEnteredTime = System.currentTimeMillis();
        }
    }

    public void update(Enemy enemy, Collection<Player> players,
                       Map<Long, Enemy> allEnemies, CollisionChecker checker, Server server) {
        currentState.update(this, enemy, players, allEnemies, checker, server);
    }

    public long getTimeInCurrentState() {
        return System.currentTimeMillis() - stateEnteredTime;
    }

    public double getChaseRange() { return CHASE_RANGE; }
    public double getAttackRange() { return ATTACK_RANGE; }
    public double getFleeHpThreshold() { return FLEE_HP_THRESHOLD; }
}
