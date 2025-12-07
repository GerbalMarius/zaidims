package org.game.entity.enemy.skeleton;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class BigSkeleton extends  Skeleton {

    public BigSkeleton() {
        this(0, 0);
    }
    public BigSkeleton(int x, int y) {
        super(x, y);
        this.size = EnemySize.BIG;
        scale = 5;
        createHitbox();
        setPiercingFactor(0.4);
    }

    @Override
    public Prototype createDeepCopy() {
        BigSkeleton bigSkeleton = new BigSkeleton(this.globalX, this.globalY);
        copyCombatStatsTo(bigSkeleton);
        return bigSkeleton;
    }
}
