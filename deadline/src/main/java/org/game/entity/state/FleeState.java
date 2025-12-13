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

public final class FleeState implements EnemyState {
    private static final FleeState INSTANCE = new FleeState();
    private static final double RECOVERY_HP_THRESHOLD = 0.5;
    private final Random random = new Random();

    private FleeState() {}

    public static FleeState getInstance() { return INSTANCE; }

    @Override
    public void update(EnemyStateContext context, Enemy enemy, Collection<Player> players,
                       Map<Long, Enemy> allEnemies, CollisionChecker checker, Server server) {

        double hpRatio = (double) enemy.getHitPoints() / enemy.getMaxHitPoints();
        if (hpRatio >= RECOVERY_HP_THRESHOLD) {
            context.setState(IdleState.getInstance());
            return;
        }

        Player threat = enemy.getClosestPlayer(players);
        if (threat == null) {
            // No players, just wander away randomly
            performRandomFlee(enemy, allEnemies, checker);
            return;
        }

        performFlee(enemy, threat, allEnemies, checker);
    }

    private void performFlee(Enemy enemy, Player threat, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        int dx = Integer.compare(enemy.getGlobalX(), threat.getGlobalX());
        int dy = Integer.compare(enemy.getGlobalY(), threat.getGlobalY());

        // If we're at the same position, pick a random direction
        if (dx == 0 && dy == 0) {
            dx = random.nextBoolean() ? 1 : -1;
            dy = random.nextBoolean() ? 1 : -1;
        }

        List<Enemy> others = allEnemies.values().stream().filter(e -> e != enemy).toList();

        // Update direction for animation
        if (Math.abs(dx) >= Math.abs(dy)) {
            enemy.setDirection(dx > 0 ? FramePosition.RIGHT : FramePosition.LEFT);
        } else {
            enemy.setDirection(dy > 0 ? FramePosition.DOWN : FramePosition.UP);
        }

        // Flee faster (1.5x speed)
        int fleeSpeed = (int) (enemy.getSpeed() * 1.5);
        enemy.tryMove(dx * fleeSpeed, dy * fleeSpeed, others, checker);
    }

    private void performRandomFlee(Enemy enemy, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        int dx = 0, dy = 0;
        switch (enemy.getDirection()) {
            case LEFT -> dx = -1;
            case RIGHT -> dx = 1;
            case UP -> dy = -1;
            case DOWN -> dy = 1;
        }

        List<Enemy> others = allEnemies.values().stream().filter(e -> e != enemy).toList();
        int fleeSpeed = (int) (enemy.getSpeed() * 1.5);
        enemy.tryMove(dx * fleeSpeed, dy * fleeSpeed, others, checker);
    }

    @Override
    public String getStateName() { return "FLEE"; }
}
