package org.game.entity.enemy.skeleton;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class SmallSkeleton extends Skeleton {

    public SmallSkeleton() {
        this(0, 0);
    }

    public SmallSkeleton(int x, int y) {
        super(x, y);
        this.size = EnemySize.SMALL;
        scale = 3;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        SmallSkeleton smallSkeleton = new SmallSkeleton(this.globalX, this.globalY);
        copyCombatStatsTo(smallSkeleton);
        return  smallSkeleton;
    }
}
