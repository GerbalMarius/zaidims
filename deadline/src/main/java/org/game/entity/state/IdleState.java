package org.game.entity.state;

import org.game.entity.Enemy;
import org.game.entity.FramePosition;
import org.game.entity.Player;
import org.game.server.CollisionChecker;
import org.game.server.Server;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class IdleState implements EnemyState {
    private static final IdleState INSTANCE = new IdleState();
    private static final long IDLE_DURATION_MS = 2000;
    private final Random random = new Random();

    private IdleState() {}

    public static IdleState getInstance() { return INSTANCE; }

    @Override
    public void update(EnemyStateContext context, Enemy enemy, Collection<Player> players,
                       Map<Long, Enemy> allEnemies, CollisionChecker checker, Server server) {

        // Check for low HP -> Flee
        double hpRatio = (double) enemy.getHitPoints() / enemy.getMaxHitPoints();
        if (hpRatio <= context.getFleeHpThreshold()) {
            context.setState(FleeState.getInstance());
            return;
        }

        // Check for nearby players -> Chase
        Player nearest = enemy.getClosestPlayer(players);
        if (nearest != null) {
            double dist = Math.hypot(
                    nearest.getGlobalX() - enemy.getGlobalX(),
                    nearest.getGlobalY() - enemy.getGlobalY()
            );
            if (dist <= context.getChaseRange()) {
                context.setLastKnownTarget(nearest);
                context.setState(ChaseState.getInstance());
                return;
            }
        }

        // Always perform some movement when idle (random wandering)
        performRandomMovement(enemy, allEnemies, checker);
    }

    private void performRandomMovement(Enemy enemy, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        // Higher chance to change direction for more active movement
        if (random.nextInt(100) < 3) {
            FramePosition[] dirs = FramePosition.values();
            FramePosition randomDir = dirs[random.nextInt(dirs.length)];
            enemy.setDirection(randomDir);
        }

        int dx = 0, dy = 0;
        switch (enemy.getDirection()) {
            case LEFT -> dx = -1;
            case RIGHT -> dx = 1;
            case UP -> dy = -1;
            case DOWN -> dy = 1;
        }

        List<Enemy> others = allEnemies.values().stream().filter(e -> e != enemy).toList();
        enemy.tryMove(dx * enemy.getSpeed(), dy * enemy.getSpeed(), others, checker);
    }

    @Override
    public String getStateName() { return "IDLE"; }
}
