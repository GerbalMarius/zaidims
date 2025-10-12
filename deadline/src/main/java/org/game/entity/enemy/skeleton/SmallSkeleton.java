package org.game.entity.enemy.skeleton;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class SmallSkeleton extends Skeleton {

    public SmallSkeleton(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.SMALL;
    }

    @Override
    protected void configureStats() {
        maxHitPoints = hitPoints = 20;
        attack = 7;
        scale = 3;
        speed = 4;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        return new SmallSkeleton(this.globalX, this.globalY);
    }
}
