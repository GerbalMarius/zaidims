package org.game.entity.enemy.wave;

import org.game.server.Server;
import org.game.tiles.TileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class EnemyGroup implements WaveGroup {
    private final List<WaveGroup> childGroups = new ArrayList<>();

    public void addChildGroup(WaveGroup group) {
        childGroups.add(group);
    }

    @Override
    public void spawn(Server server, TileManager tileManager, AtomicLong enemyId, Random random) {
        for (WaveGroup group : childGroups) {
            group.spawn(server, tileManager, enemyId, random);
        }
    }

    @Override
    public int size() {
        return childGroups.stream()
                .reduce(0, (acc, group) -> acc + group.size(), Integer::sum);
    }
}
