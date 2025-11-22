package org.game.entity.strategy;

import org.game.entity.Enemy;
import org.game.entity.Player;
import org.game.server.CollisionChecker;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FollowLeaderStrategy implements EnemyStrategy {
    private final Enemy leader;
    private final int offsetX;
    private final int offsetY;

    public FollowLeaderStrategy(Enemy leader, int offsetX, int offsetY) {
        this.leader = leader;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void execute(Enemy self, Collection<Player> players, Map<Long, Enemy> allEnemies, CollisionChecker checker) {
        if (leader == null || leader.isDead()) {
            self.setStrategy(new WanderStrategy());
            return;
        }

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
}
