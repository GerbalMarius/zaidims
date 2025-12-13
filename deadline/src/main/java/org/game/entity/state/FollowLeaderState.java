package org.game.entity.state;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.CollisionChecker;
import org.game.server.Server;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class FollowLeaderState implements EnemyState {
    private final Enemy leader;
    private final int offsetX;
    private final int offsetY;

    public FollowLeaderState(Enemy leader, int offsetX, int offsetY) {
        this.leader = leader;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void update(EnemyStateContext context, Enemy enemy, Collection<Player> players,
                       Map<Long, Enemy> allEnemies, CollisionChecker checker, Server server) {

        if (leader == null || leader.isDead()) {
            context.setState(IdleState.getInstance());
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

        performFollowLeader(enemy, allEnemies, checker);
    }

    private void performFollowLeader(Enemy self, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        List<Enemy> otherEnemies = allEnemies.values().stream()
                .filter(e -> e != self)
                .toList();

        int targetX = leader.getGlobalX() + offsetX;
        int targetY = leader.getGlobalY() + offsetY;

        int dx = targetX - self.getGlobalX();
        int dy = targetY - self.getGlobalY();

        int distance = (int) Math.hypot(dx, dy);
        if (distance == 0) return;

        int stepX = (int) Math.round(self.getSpeed() * dx / (double) distance);
        int stepY = (int) Math.round(self.getSpeed() * dy / (double) distance);

        self.tryMove(stepX, stepY, otherEnemies, checker);
    }

    @Override
    public String getStateName() { return "FOLLOW_LEADER"; }
    @Override
    public Set<Class<? extends EnemyState>> getAllowedTransitions() {
        return Set.of(IdleState.class, ChaseState.class);
    }
}


