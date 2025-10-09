package org.game.entity.enemy.zombie;

import org.game.entity.EnemySize;

public class BigZombie extends Zombie {

    public BigZombie(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.BIG;
    }

    @Override
    public void configureStats() {
        hitPoints = 120;
        attack = 25;
        scale = 5;
        speed = 1;
        createHitbox();
    }
}
