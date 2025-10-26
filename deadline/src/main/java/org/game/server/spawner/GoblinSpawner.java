package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.enemy.goblin.BigGoblin;
import org.game.entity.enemy.goblin.Goblin;
import org.game.entity.enemy.goblin.MediumGoblin;
import org.game.entity.enemy.goblin.SmallGoblin;

public class GoblinSpawner implements EnemySpawner {

    @Override
    public Enemy spawnSmall(long id,int x, int y) {
      return  Goblin.builder()
                .withEnemy(SmallGoblin::new)
                .withId(id)
                .atPos(x, y)
                .withAttack(8)
                .withHp(25)
                .withSpeed(5)
                .build();
    }

    @Override
    public Enemy spawnMedium(long id,int x, int y) {
       return Goblin.builder()
                .withEnemy(MediumGoblin::new)
                .withId(id)
                .atPos(x, y)
                .withAttack(15)
                .withHp(50)
                .withSpeed(4)
                .build();
    }

    @Override
    public Enemy spawnLarge(long id, int x, int y) {
        return  Goblin.builder()
                .withEnemy(BigGoblin::new)
                .withId(id)
                .atPos(x, y)
                .withAttack(22)
                .withHp(80)
                .withSpeed(3)
                .build();
    }
}
