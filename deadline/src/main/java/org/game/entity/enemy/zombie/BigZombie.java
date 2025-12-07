package org.game.entity.enemy.zombie;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class BigZombie extends Zombie {
    public BigZombie() {
        this(0, 0);
    }
    public BigZombie(int x, int y) {
        super(x, y);
        this.size = EnemySize.BIG;
        scale = 5;
        createHitbox();
        setPiercingFactor(0.5);
    }

    @Override
    public Prototype createDeepCopy() {
        BigZombie bigZombie = new BigZombie(this.globalX, this.globalY);
        copyCombatStatsTo(bigZombie);
        return bigZombie;
    }
}
