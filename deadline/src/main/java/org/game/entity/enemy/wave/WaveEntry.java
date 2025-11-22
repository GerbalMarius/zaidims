package org.game.entity.enemy.wave;

import org.game.server.Server;
import org.game.tiles.TileManager;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public interface WaveEntry {

    void spawn(Server server, TileManager tileManager,
               AtomicLong enemyId, Random random);

    int size();
}
