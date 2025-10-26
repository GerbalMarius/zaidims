package org.game.server.spawner;

import org.game.entity.Enemy;

public interface EnemySpawner {

    Enemy spawnSmall(long id,int x, int y);
    Enemy spawnMedium(long id, int x, int y);
    Enemy spawnLarge(long id,int x, int y);

}
