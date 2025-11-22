package org.game.entity.enemy.wave;

import org.game.entity.Enemy;
import org.game.server.Server;
import org.game.tiles.TileManager;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class CircleFormation implements WaveGroup {

    private final Enemy prototype;
    private final int count;
    private final int radius;
    private final int hpGrowth;
    private final int damageGrowth;

    public CircleFormation(Enemy prototype, int count, int radius, int hpGrowth, int damageGrowth) {
        this.prototype = prototype;
        this.count = count;
        this.radius = radius;
        this.hpGrowth = hpGrowth;
        this.damageGrowth = damageGrowth;
    }

    @Override
    public void spawn(Server server,
                      TileManager tileManager,
                      AtomicLong enemyId,
                      Random random) {

        Point center = tileManager.findRandomSpawnPosition(random, 50);

        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            int x = center.x + (int) (radius * Math.cos(angle));
            int y = center.y + (int) (radius * Math.sin(angle));

            Enemy enemy = Scaler.scaleEnemy(enemyId, prototype, hpGrowth, damageGrowth);
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
