package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.enemy.zombie.BigZombie;
import org.game.entity.enemy.zombie.MediumZombie;
import org.game.entity.enemy.zombie.SmallZombie;
import org.game.entity.enemy.zombie.Zombie;

public class ZombieSpawner implements EnemySpawner{

    @Override
    public Enemy spawnSmall(long id, int x, int y) {
        return Zombie.builder()
                .withEnemy(SmallZombie::new)
                .withId(id)
                .atPos(x, y)
                .withAttack(5)
                .withHp(30)
                .withSpeed(3)
                .build();
    }

    @Override
    public Enemy spawnMedium(long id, int x, int y) {
       return Zombie.builder()
                .withEnemy(MediumZombie::new)
                .withId(id)
                .atPos(x, y)
                .withAttack(10)
                .withHp(60)
                .withSpeed(2)
                .build();
    }

    @Override
    public Enemy spawnLarge(long id, int x, int y) {
        return Zombie.builder()
                .withEnemy(BigZombie::new)
                .withId(id)
                .atPos(x, y)
                .withHp(120)
                .withAttack(25)
                .withSpeed(1)
                .build();
    }
}
