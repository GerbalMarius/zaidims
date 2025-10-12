package org.game.entity.enemy.skeleton;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class MediumSkeleton extends Skeleton {

    public MediumSkeleton(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.MEDIUM;
    }

    @Override
    protected void configureStats() {
        maxHitPoints = hitPoints = 40;
        attack = 12;
        scale = 4;
        speed = 3;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        return new MediumSkeleton(this.globalX, this.globalY);
    }
}
