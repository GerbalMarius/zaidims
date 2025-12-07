package org.game.entity.enemy.goblin;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class BigGoblin extends Goblin {

    public BigGoblin() {
        this(0, 0);
    }

    public BigGoblin(int x, int y) {
        super(x, y);
        this.size = EnemySize.BIG;
        scale = 5;
        createHitbox();
        setPiercingFactor(0.3);
    }

    @Override
    public Prototype createDeepCopy() {
        BigGoblin bigGoblin = new BigGoblin(this.globalX, this.globalY);
        copyCombatStatsTo(bigGoblin);
        return bigGoblin;
    }
}
