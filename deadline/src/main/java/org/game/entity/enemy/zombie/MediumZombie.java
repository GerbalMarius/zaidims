package org.game.entity.enemy.zombie;

import org.game.entity.EnemySize;
import org.game.server.Prototype;

public class MediumZombie extends Zombie{

    public MediumZombie() {
        this(0, 0);
    }

    public MediumZombie(int x, int y) {
        super(x, y);
        this.size = EnemySize.MEDIUM;
        scale = 4;
        createHitbox();
    }


    @Override
    public Prototype createDeepCopy() {
        MediumZombie mediumZombie = new MediumZombie(this.globalX, this.globalY);
        copyCombatStatsTo(mediumZombie);
        return mediumZombie;
    }
}
