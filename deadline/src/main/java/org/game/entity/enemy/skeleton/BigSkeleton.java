package org.game.entity.enemy.skeleton;

import org.game.entity.EnemySize;

public class BigSkeleton extends  Skeleton {

    public BigSkeleton(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.BIG;
    }

    @Override
    protected void configureStats() {
        maxHitPoints = hitPoints = 90;
        attack = 25;
        scale = 5;
        speed = 2;
        createHitbox();
    }
}
