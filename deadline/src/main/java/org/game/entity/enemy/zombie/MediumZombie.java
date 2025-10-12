package org.game.entity.enemy.zombie;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class MediumZombie extends Zombie{

    public MediumZombie(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.MEDIUM;
    }

    @Override
    protected void configureStats() {
        maxHitPoints = hitPoints = 60;
        attack = 10;
        scale = 4;
        speed = 2;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        return new MediumZombie(this.globalX, this.globalY);
    }
}
