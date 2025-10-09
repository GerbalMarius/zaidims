package org.game.entity.enemy.goblin;

import org.game.entity.EnemySize;

public class SmallGoblin extends Goblin {

    public SmallGoblin(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.SMALL;
    }

    @Override
    protected void configureStats() {
        hitPoints = 25;
        attack = 8;
        scale = 3;
        speed = 5;
        createHitbox();
    }
}
