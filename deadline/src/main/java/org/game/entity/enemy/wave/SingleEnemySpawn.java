package org.game.entity.enemy.wave;

import org.game.entity.Enemy;
import org.game.server.Server;
import org.game.server.Server.ServerActions;
import org.game.tiles.TileManager;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SingleEnemySpawn implements WaveEntry {
    protected final Enemy prototype;
    protected final int hpGrowth;
    protected final int damageGrowth;

    public SingleEnemySpawn(Enemy prototype, int hpGrowth, int damageGrowth) {
        this.prototype = prototype;
        this.hpGrowth = hpGrowth;
        this.damageGrowth = damageGrowth;
    }

    @Override
    public void spawn(Server server,
                      TileManager tileManager,
                      AtomicLong enemyId,
                      Random random) {

        Enemy enemy = Scaler.scaleEnemy(enemyId, prototype, hpGrowth, damageGrowth);

        Point spawnPos = tileManager.findRandomSpawnPosition(random, 50);
        enemy.setGlobalX(spawnPos.x);
        enemy.setGlobalY(spawnPos.y);

        ServerActions.spawnEnemy(server, enemy, spawnPos.x, spawnPos.y);
    }

    @Override
    public int size() {
        return 1;
    }
}
