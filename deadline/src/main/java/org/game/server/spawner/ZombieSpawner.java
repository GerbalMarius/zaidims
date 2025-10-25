package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemyBuilder;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.entity.enemy.zombie.BigZombie;
import org.game.entity.enemy.zombie.MediumZombie;
import org.game.entity.enemy.zombie.SmallZombie;

public class ZombieSpawner implements EnemySpawner{

    private final EnemyBuilder prototype;

    public ZombieSpawner(){
        this.prototype = new EnemyBuilder().ofType(EnemyType.ZOMBIE);
    }

    @Override
    public Enemy spawnSmall(int x, int y) {
        return prototype.copy().withSize(EnemySize.SMALL).at(x,y).build();
    }

    @Override
    public Enemy spawnMedium(int x, int y) {
        return prototype.copy().withSize(EnemySize.MEDIUM).at(x,y).build();
    }

    @Override
    public Enemy spawnLarge(int x, int y) {
        return prototype.copy().withSize(EnemySize.BIG).at(x,y).build();
    }
}
