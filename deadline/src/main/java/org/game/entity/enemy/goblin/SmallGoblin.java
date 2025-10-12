package org.game.entity.enemy.goblin;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class SmallGoblin extends Goblin {

    public SmallGoblin(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.SMALL;
    }

    @Override
    protected void configureStats() {
        maxHitPoints = hitPoints = 25;
        attack = 8;
        scale = 3;
        speed = 5;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        return new SmallGoblin(this.globalX, this.globalY);
    }
}
