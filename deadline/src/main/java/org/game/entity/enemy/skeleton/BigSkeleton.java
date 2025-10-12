package org.game.entity.enemy.skeleton;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

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

    @Override
    public Prototype createDeepCopy() {
        return new BigSkeleton(this.globalX, this.globalY);
    }
}
