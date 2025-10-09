package org.game.entity.enemy.skeleton;

import org.game.entity.Enemy;
import org.game.entity.EnemyType;

public abstract class Skeleton extends Enemy {

    protected Skeleton(int x, int y) {
        super(x, y);

        loadSprite("ske", "enemy");
        this.type = EnemyType.SKELETON;
    }
}
