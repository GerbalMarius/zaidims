package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.enemy.skeleton.BigSkeleton;
import org.game.entity.enemy.skeleton.MediumSkeleton;
import org.game.entity.enemy.skeleton.SmallSkeleton;

public class SkeletonSpawner implements EnemySpawner{


    @Override
    public Enemy spawnSmall(int x, int y) {
        return new SmallSkeleton(x, y);
    }

    @Override
    public Enemy spawnMedium(int x, int y) {
        return new MediumSkeleton(x, y);
    }

    @Override
    public Enemy spawnLarge(int x, int y) {
        return new BigSkeleton(x, y);
    }
}
