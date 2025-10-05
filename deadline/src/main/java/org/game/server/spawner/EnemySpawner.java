package org.game.server.spawner;

import org.game.entity.Enemy;

public interface EnemySpawner {

    Enemy spawnSmall(int x, int y);
    Enemy spawnMedium(int x, int y);
    Enemy spawnLarge(int x, int y);
}
