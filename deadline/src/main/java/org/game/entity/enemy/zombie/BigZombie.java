package org.game.entity.enemy.zombie;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class BigZombie extends Zombie {

    public BigZombie(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.BIG;
    }

    @Override
    protected void configureStats() {
        maxHitPoints = hitPoints = 120;
        attack = 25;
        scale = 5;
        speed = 1;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        return new BigZombie(this.globalX, this.globalY);
    }
}
