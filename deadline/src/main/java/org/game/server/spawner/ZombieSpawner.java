package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.entity.enemy.zombie.BigZombie;
import org.game.entity.enemy.zombie.MediumZombie;
import org.game.entity.enemy.zombie.SmallZombie;

public class ZombieSpawner implements EnemySpawner{

    @Override
    public Enemy spawnSmall(int x, int y) {
        return new SmallZombie(x, y);
    }

    @Override
    public Enemy spawnMedium(int x, int y) {
        return new MediumZombie(x, y);
    }

    @Override
    public Enemy spawnLarge(int x, int y) {
        return new BigZombie(x, y);
    }
}
