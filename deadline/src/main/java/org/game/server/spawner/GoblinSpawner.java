package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;

public class GoblinSpawner implements EnemySpawner {

    private static final EnemyType GOBLIN = EnemyType.GOBLIN;

    @Override
    public Enemy spawnSmall(int x, int y) {
        return new Enemy(GOBLIN, EnemySize.SMALL, x, y);
    }

    @Override
    public Enemy spawnMedium(int x, int y) {
        return new Enemy(GOBLIN, EnemySize.MEDIUM, x, y);
    }

    @Override
    public Enemy spawnLarge(int x, int y) {
        return new Enemy(GOBLIN, EnemySize.BIG, x, y);
    }
}
