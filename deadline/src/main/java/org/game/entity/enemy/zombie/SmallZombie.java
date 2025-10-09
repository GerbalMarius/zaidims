package org.game.entity.enemy.zombie;

import org.game.entity.EnemySize;

public class SmallZombie extends Zombie {

    public SmallZombie(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.SMALL;
    }

    @Override
    protected void configureStats() {
        hitPoints = 30;
        attack = 5;
        scale = 3;
        speed = 3;
        createHitbox();
    }
}
