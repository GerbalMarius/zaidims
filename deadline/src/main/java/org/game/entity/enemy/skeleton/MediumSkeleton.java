package org.game.entity.enemy.skeleton;

import org.game.entity.EnemySize;

public class MediumSkeleton extends Skeleton {

    public MediumSkeleton(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.MEDIUM;
    }

    @Override
    protected void configureStats() {

        hitPoints = 40;
        attack = 12;
        scale = 4;
        speed = 3;
        createHitbox();
    }
}
