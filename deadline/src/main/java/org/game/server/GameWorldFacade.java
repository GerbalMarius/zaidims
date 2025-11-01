package org.game.server;

import org.game.server.powerup.PowerUpManager;
import org.game.server.spawner.EnemySpawnManager;
import org.game.server.spawner.EnemyUpdateManager;

import java.util.concurrent.TimeUnit;

public class GameWorldFacade {

    private final EnemySpawnManager enemySpawnManager;
    private final EnemyUpdateManager enemyUpdateManager;
    private final PowerUpManager powerUpManager;

    public GameWorldFacade(Server server) {
        this.enemySpawnManager = new EnemySpawnManager(server);
        this.enemyUpdateManager = new EnemyUpdateManager(server);
        this.powerUpManager = new PowerUpManager(server);
    }

    public void initialize() {
        //enemySpawnManager.startSpawning(0, 5, TimeUnit.SECONDS);
        //enemySpawnManager.startWaveSpawning(10, 50, TimeUnit.SECONDS);
        //enemySpawnManager.startShallowWaveSpawning(10, 50, TimeUnit.SECONDS);
        enemySpawnManager.spawnShallowWave();
        enemyUpdateManager.startUpdating(0, 50, TimeUnit.MILLISECONDS);

        powerUpManager.startDispensing(10, 15, TimeUnit.SECONDS);

        System.out.println("GameWorldFacade initialized");
    }
}
