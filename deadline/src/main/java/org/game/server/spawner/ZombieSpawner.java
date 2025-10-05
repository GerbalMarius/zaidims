package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;

public class ZombieSpawner implements EnemySpawner{

    private static final EnemyType ZOMBIE = EnemyType.ZOMBIE;

    @Override
    public Enemy spawnSmall(int x, int y) {
        return new Enemy(ZOMBIE, EnemySize.SMALL, x, y);
    }

    @Override
    public Enemy spawnMedium(int x, int y) {
        return new Enemy(ZOMBIE, EnemySize.MEDIUM, x, y);
    }

    @Override
    public Enemy spawnLarge(int x, int y) {
        return new Enemy(ZOMBIE, EnemySize.BIG, x, y);
    }
}
