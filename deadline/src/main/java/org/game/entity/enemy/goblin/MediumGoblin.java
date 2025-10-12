package org.game.entity.enemy.goblin;

import org.game.entity.EnemySize;

public class MediumGoblin extends Goblin {

    public MediumGoblin(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.MEDIUM;
    }

    @Override
    protected void configureStats() {
        maxHitPoints = hitPoints = 50;
        attack = 15;
        scale = 4;
        speed = 4;
        createHitbox();
    }
}
