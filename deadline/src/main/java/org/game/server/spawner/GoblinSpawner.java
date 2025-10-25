package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.enemy.goblin.BigGoblin;
import org.game.entity.enemy.goblin.MediumGoblin;
import org.game.entity.enemy.goblin.SmallGoblin;

public class GoblinSpawner implements EnemySpawner {


    @Override
    public Enemy spawnSmall(int x, int y) {
        return new SmallGoblin(x, y);
    }

    @Override
    public Enemy spawnMedium(int x, int y) {
        return new MediumGoblin(x, y);
    }

    @Override
    public Enemy spawnLarge(int x, int y) {
        return new BigGoblin(x, y);
    }
}
