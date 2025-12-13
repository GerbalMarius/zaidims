package org.game.entity.enemy.wave;

import org.game.entity.Enemy;
import org.game.entity.state.FollowLeaderState;
import org.game.server.Server;
import org.game.server.Server.ServerActions;
import org.game.tiles.TileManager;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class SquadGroup extends SingleEnemySpawn implements WaveEntry {

    private final Enemy followerPrototype;
    private final int count;
    private final int spacing;
    private static int nextGroupId = 1;

    public SquadGroup(Enemy leaderPrototype,
                          Enemy followerPrototype,
                          int count,
                          int spacing,
                          int hpGrowth,
                          int damageGrowth) {
        super(leaderPrototype, hpGrowth, damageGrowth);
        this.followerPrototype = followerPrototype;
        this.count = count;
        this.spacing = spacing;
    }

    @Override
    public void spawn(Server server,
                      TileManager tileManager,
                      AtomicLong enemyId,
                      Random random) {

        int groupId = nextGroupId++;

        Point base = tileManager.findRandomSpawnPosition(random, 50);
        int centerX = base.x;
        int centerY = base.y;

        // 1) spawn leader in center
        Enemy leader = (Enemy) prototype.createDeepCopy();
        applyGrowth(leader);

        leader.setId(enemyId.getAndIncrement());
        leader.setGlobalX(centerX);
        leader.setGlobalY(centerY);
        leader.setGroupId(groupId);
        leader.setGroupLeader(true);

        ServerActions.spawnEnemy(server, leader, centerX, centerY);

        int followersPerSide = (count - 1) / 2;

        for (int i = -followersPerSide; i <= followersPerSide; i++) {
            if (i == 0) continue;

            Enemy follower = (Enemy) followerPrototype.createDeepCopy();
            applyGrowth(follower);

            follower.setId(enemyId.getAndIncrement());
            int x = centerX + i * spacing;
            follower.setGlobalX(x);
            follower.setGlobalY(centerY);

            follower.setGroupId(groupId);
            follower.setGroupLeader(false);
            follower.setGroupLeaderRef(leader);

            follower.getStateContext().setState(new FollowLeaderState(leader, i * spacing, 0));

            ServerActions.spawnEnemy(server, follower, x, centerY);
        }
    }

    @Override
    public int size() {
        return count;
    }

    private void applyGrowth(Enemy enemy) {
        int hp = enemy.getMaxHitPoints() * hpGrowth / 100;
        enemy.setMaxHitPoints(hp);
        enemy.setHitPoints(hp);

        int attack = enemy.getAttack() * damageGrowth / 100;
        enemy.setAttack(attack);
    }

}
