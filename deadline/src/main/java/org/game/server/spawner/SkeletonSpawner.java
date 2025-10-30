package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.enemy.skeleton.BigSkeleton;
import org.game.entity.enemy.skeleton.MediumSkeleton;
import org.game.entity.enemy.skeleton.Skeleton;
import org.game.entity.enemy.skeleton.SmallSkeleton;

public class SkeletonSpawner implements EnemySpawner {


    @Override
    public Enemy spawnSmall(long id,int x, int y) {
       return Skeleton.builder()
                .withEnemy(SmallSkeleton::new)
                .withId(id)
                .atPos(x, y)
                .withAttack(7)
                .withHp(20)
                .withSpeed(4)
                .build();
    }

    @Override
    public Enemy spawnMedium(long id, int x, int y) {
       return Skeleton.builder()
                .withEnemy(MediumSkeleton::new)
                .withId(id)
                .atPos(x, y)
                .withAttack(12)
                .withHp(40)
                .withSpeed(3)
                .build();
    }

    @Override
    public Enemy spawnLarge(long id, int x, int y) {
        return Skeleton.builder()
                .withEnemy(BigSkeleton::new)
                .withId(id)
                .atPos(x, y)
                .withAttack(25)
                .withHp(40)
                .withSpeed(2)
                .build();
    }
}
