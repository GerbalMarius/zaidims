package org.game.entity.enemy.goblin;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class MediumGoblin extends Goblin {

    public MediumGoblin() {
        this(0, 0);
    }
    public MediumGoblin(int x, int y) {
        super(x, y);
        this.size = EnemySize.MEDIUM;
        scale = 4;
        createHitbox();
    }



    @Override
    public Prototype createDeepCopy() {
        MediumGoblin mediumGoblin = new MediumGoblin(this.globalX, this.globalY);
        copyCombatStatsTo(mediumGoblin);
        return mediumGoblin;
    }
}
