package org.game.entity.state;

import org.game.entity.Enemy;
import org.game.entity.FramePosition;
import org.game.entity.Player;
import org.game.server.CollisionChecker;
import org.game.server.Server;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ChaseState implements EnemyState {
    private static final ChaseState INSTANCE = new ChaseState();
    private static final double LOSE_INTEREST_RANGE = 700.0;

    private ChaseState() {}

    public static ChaseState getInstance() { return INSTANCE; }

    @Override
    public void update(EnemyStateContext context, Enemy enemy, Collection<Player> players,
                       Map<Long, Enemy> allEnemies, CollisionChecker checker, Server server) {

        double hpRatio = (double) enemy.getHitPoints() / enemy.getMaxHitPoints();
        if (hpRatio <= context.getFleeHpThreshold()) {
            context.setState(FleeState.getInstance());
            return;
        }

        Player target = enemy.getClosestPlayer(players);
        if (target == null || target.isDead()) {
            context.setState(IdleState.getInstance());
            return;
        }

        double dist = Math.hypot(
                target.getGlobalX() - enemy.getGlobalX(),
                target.getGlobalY() - enemy.getGlobalY()
        );

        if (dist > LOSE_INTEREST_RANGE) {
            context.setState(IdleState.getInstance());
            return;
        }

        context.setLastKnownTarget(target);

        performChase(enemy, target, allEnemies, checker);
    }

    private void performChase(Enemy enemy, Player target, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        int dx = Integer.compare(target.getGlobalX(), enemy.getGlobalX());
        int dy = Integer.compare(target.getGlobalY(), enemy.getGlobalY());

        List<Enemy> others = allEnemies.values().stream().filter(e -> e != enemy).toList();

        // Update direction based on movement
        if (Math.abs(dx) >= Math.abs(dy)) {
            if (dx != 0) enemy.setDirection(dx > 0 ? FramePosition.RIGHT : FramePosition.LEFT);
        } else {
            if (dy != 0) enemy.setDirection(dy > 0 ? FramePosition.DOWN : FramePosition.UP);
        }

        // Move towards target
        enemy.tryMove(dx * enemy.getSpeed(), dy * enemy.getSpeed(), others, checker);
    }

    @Override
    public String getStateName() { return "CHASE"; }
}
