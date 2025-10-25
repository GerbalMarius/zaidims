package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemyBuilder;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.entity.enemy.skeleton.BigSkeleton;
import org.game.entity.enemy.skeleton.MediumSkeleton;
import org.game.entity.enemy.skeleton.SmallSkeleton;

public class SkeletonSpawner implements EnemySpawner{

    private final EnemyBuilder prototype;

    public SkeletonSpawner(){
        this.prototype = new EnemyBuilder().ofType(EnemyType.SKELETON);
    }

    @Override
    public Enemy spawnSmall(int x, int y) {
        return prototype.copy().withSize(EnemySize.SMALL).at(x,y).build();
    }

    @Override
    public Enemy spawnMedium(int x, int y) {
        return prototype.copy().withSize(EnemySize.MEDIUM).at(x,y).build();
    }

    @Override
    public Enemy spawnLarge(int x, int y) {
        return prototype.copy().withSize(EnemySize.BIG).at(x,y).build();
    }
}
