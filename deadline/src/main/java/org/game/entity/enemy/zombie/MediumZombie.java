package org.game.entity.enemy.zombie;

import org.game.entity.EnemySize;

public class MediumZombie extends Zombie{

    public MediumZombie(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.MEDIUM;
    }

    @Override
    protected void configureStats() {
        hitPoints = 60;
        attack = 10;
        scale = 4;
        speed = 2;
        createHitbox();
    }
}
