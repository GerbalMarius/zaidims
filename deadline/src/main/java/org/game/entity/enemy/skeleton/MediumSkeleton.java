package org.game.entity.enemy.skeleton;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class MediumSkeleton extends Skeleton {


    public MediumSkeleton() {
        this(0, 0);
        loadSprite("ske", "enemy");
    }
    public MediumSkeleton(int x, int y) {
        super(x, y);
        this.size = EnemySize.MEDIUM;
        scale = 4;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        MediumSkeleton mediumSkeleton = new MediumSkeleton(this.globalX, this.globalY);
        copyCombatStatsTo(mediumSkeleton);
        return mediumSkeleton;
    }
}
