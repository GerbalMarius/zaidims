package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;

public class SkeletonSpawner implements EnemySpawner{

    private static final EnemyType SKELETON = EnemyType.SKELETON;

    @Override
    public Enemy spawnSmall(int x, int y) {
        return new Enemy(SKELETON, EnemySize.SMALL, x, y);
    }

    @Override
    public Enemy spawnMedium(int x, int y) {
        return new Enemy(SKELETON, EnemySize.MEDIUM, x, y);
    }

    @Override
    public Enemy spawnLarge(int x, int y) {
        return new Enemy(SKELETON, EnemySize.BIG, x, y);
    }
}
