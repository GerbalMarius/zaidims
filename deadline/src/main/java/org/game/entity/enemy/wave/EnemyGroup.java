package org.game.entity.enemy.wave;

import org.game.server.Server;
import org.game.tiles.TileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class EnemyGroup implements WaveEntry {
    private final List<WaveEntry> childEntries = new ArrayList<>();

    public void addChildEntry(WaveEntry entry) {
        childEntries.add(entry);
    }

    @Override
    public void spawn(Server server, TileManager tileManager, AtomicLong enemyId, Random random) {
        for (WaveEntry group : childEntries) {
            group.spawn(server, tileManager, enemyId, random);
        }
    }

    @Override
    public int size() {
        return childEntries.stream()
                .reduce(0,
                        (acc, group) -> acc + group.size(),
                        Integer::sum);
    }
}
