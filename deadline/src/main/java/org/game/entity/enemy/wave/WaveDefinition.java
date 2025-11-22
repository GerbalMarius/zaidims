package org.game.entity.enemy.wave;

import lombok.Getter;
import org.game.server.Server;
import org.game.tiles.TileManager;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class WaveDefinition {

    @Getter
    private final int waveNumber;
    private final WaveEntry rootComponent;

    public WaveDefinition(int waveNumber,
                          WaveEntry rootComponent) {
        this.waveNumber = waveNumber;
        this.rootComponent = rootComponent;
    }

    public void spawn(Server server,
                      TileManager tileManager,
                      AtomicLong enemyId,
                      Random random) {
        rootComponent.spawn(server, tileManager, enemyId, random);
    }
    public int size() {
        return rootComponent.size();
    }
}
