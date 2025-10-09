package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.enemy.goblin.SmallGoblin;
import org.game.entity.enemy.skeleton.BigSkeleton;
import org.game.entity.enemy.skeleton.MediumSkeleton;

public class GoblinSpawner implements EnemySpawner {


    @Override
    public Enemy spawnSmall(int x, int y) {
        return new SmallGoblin(x, y);
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
