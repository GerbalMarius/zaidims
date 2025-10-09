package org.game.entity.enemy.zombie;

import org.game.entity.Enemy;
import org.game.entity.EnemyType;

public abstract class Zombie extends Enemy {

    protected Zombie(int x, int y) {
        super(x, y);

        loadSprite("zom", "enemy");
        this.type = EnemyType.ZOMBIE;
    }
}
