package org.game.entity.enemy.zombie;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class SmallZombie extends Zombie {

    public SmallZombie() {
        this(0, 0);
    }

    public SmallZombie(int x, int y) {
        super(x, y);
        scale = 3;
        this.size = EnemySize.SMALL;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        SmallZombie smallZombie = new SmallZombie(this.globalX, this.globalY);
        copyCombatStatsTo(smallZombie);
        return smallZombie;
    }
}
