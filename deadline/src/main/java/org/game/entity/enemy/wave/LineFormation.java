package org.game.entity.enemy.wave;

import org.game.entity.Enemy;
import org.game.server.Server;
import org.game.tiles.TileManager;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class LineFormation implements WaveGroup {

    private final Enemy prototype;
    private final int count;
    private final int spacing;       // distance between enemies
    private final int hpGrowth;
    private final int damageGrowth;

    public LineFormation(Enemy prototype,
                         int count,
                         int spacing,
                         int hpGrowth,
                         int damageGrowth) {
        this.prototype = prototype;
        this.count = count;
        this.spacing = spacing;
        this.hpGrowth = hpGrowth;
        this.damageGrowth = damageGrowth;
    }

    @Override
    public void spawn(Server server,
                      TileManager tileManager,
                      AtomicLong enemyId,
                      Random random) {

        Point base = tileManager.findRandomSpawnPosition(random, 50);

        int startX = base.x - (count - 1) * spacing / 2;
        int y      = base.y;

        for (int i = 0; i < count; i++) {
            Enemy enemy = Scaler.scaleEnemy(enemyId, prototype, hpGrowth, damageGrowth);

            int x = startX + i * spacing;
            enemy.setGlobalX(x);
            enemy.setGlobalY(y);

            Server.ServerActions.spawnEnemy(server, enemy, x, y);
        }
    }



    @Override
    public int size() {
        return count;
    }
}
