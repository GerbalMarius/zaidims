package org.game.entity.enemy.goblin;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class SmallGoblin extends Goblin {


    public SmallGoblin() {
        this(0, 0);
    }
    public SmallGoblin(int x, int y) {
        super(x, y);
        this.size = EnemySize.SMALL;
        scale = 3;
        createHitbox();
    }

    @Override
    public Prototype createDeepCopy() {

        SmallGoblin smallGoblin = new SmallGoblin(this.globalX, this.globalY);
        copyCombatStatsTo(smallGoblin);
        return smallGoblin;
    }
}
