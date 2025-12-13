package org.game.entity.state;

import org.game.entity.Enemy;
import org.game.entity.FramePosition;
import org.game.entity.Player;
import org.game.server.CollisionChecker;
import org.game.server.Server;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ZigZagChaseState implements EnemyState {
    private static final ZigZagChaseState INSTANCE = new ZigZagChaseState();
    private static final double LOSE_INTEREST_RANGE = 700.0;

    // zigzag width
    private static final int ZIGZAG_WIDTH = 40;

    private final Map<Long, ZigzagData> enemyZigzagState = new ConcurrentHashMap<>();

    private static class ZigzagData {
        int step = 0;
        boolean goingRight = true;
    }

    private ZigZagChaseState() {}

    public static ZigZagChaseState getInstance() { return INSTANCE; }

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

        performZigZagChase(enemy, target, allEnemies, checker);
    }

    private void performZigZagChase(Enemy enemy, Player target, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        // Get or create zigzag data for this specific enemy
        ZigzagData data = enemyZigzagState.computeIfAbsent(enemy.getId(), k -> new ZigzagData());

        int dx = Integer.compare(target.getGlobalX(), enemy.getGlobalX());
        int dy = Integer.compare(target.getGlobalY(), enemy.getGlobalY());

        // Zigzag perpendicular to the main movement direction
        int zigzagOffset = data.goingRight ? 1 : -1;

        int moveX, moveY;

        // If moving more horizontally, zigzag vertically
        if (Math.abs(target.getGlobalX() - enemy.getGlobalX()) > Math.abs(target.getGlobalY() - enemy.getGlobalY())) {
            moveX = dx;
            moveY = dy + zigzagOffset;
        } else {
            // If moving more vertically, zigzag horizontally
            moveX = dx + zigzagOffset;
            moveY = dy;
        }

        // Update zigzag step
        data.step++;
        if (data.step >= ZIGZAG_WIDTH) {
            data.step = 0;
            data.goingRight = !data.goingRight;
        }

        List<Enemy> others = allEnemies.values().stream()
                .filter(e -> e != enemy)
                .toList();

        // Update direction for animation
        if (Math.abs(moveX) >= Math.abs(moveY)) {
            if (moveX != 0) enemy.setDirection(moveX > 0 ? FramePosition.RIGHT : FramePosition.LEFT);
        } else {
            if (moveY != 0) enemy.setDirection(moveY > 0 ? FramePosition.DOWN : FramePosition.UP);
        }

        enemy.tryMove(moveX * enemy.getSpeed(), moveY * enemy.getSpeed(), others, checker);
    }

    @Override
    public String getStateName() { return "ZIGZAG_CHASE"; }
}
