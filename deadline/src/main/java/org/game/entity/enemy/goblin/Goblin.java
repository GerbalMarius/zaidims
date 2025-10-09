package org.game.entity.enemy.goblin;

import org.game.entity.Enemy;
import org.game.entity.EnemyType;

public abstract class Goblin extends Enemy {

    protected Goblin(int x, int y) {
        super(x, y);

        loadSprite("gob", "enemy");
        this.type = EnemyType.GOBLIN;

    }
}
