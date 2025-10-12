package org.game.entity.enemy.zombie;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class SmallZombie extends Zombie {

    public SmallZombie(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.SMALL;
    }

    @Override
    protected void configureStats() {
        maxHitPoints = hitPoints = 30;
        attack = 5;
        scale = 3;
        speed = 3;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        return new SmallZombie(this.globalX, this.globalY);
    }
}
