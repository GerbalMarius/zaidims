package org.game.entity.enemy.goblin;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class BigGoblin extends Goblin {

    public BigGoblin(int x, int y) {
        super(x, y);
        configureStats();
        this.size = EnemySize.BIG;
    }

    @Override
    protected void configureStats() {
        maxHitPoints = hitPoints = 80;
        attack = 22;
        scale = 5;
        speed = 3;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {
        return new BigGoblin(this.globalX, this.globalY);
    }
}
