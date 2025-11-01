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
    public void startSpawningWaves(long initialDelay, long period, TimeUnit timeUnit){
        enemySpawnManager.startWaveSpawning(initialDelay, period, timeUnit);
    }

    public void startSpawningIndividualEnemies(long initialDelay, long period, TimeUnit timeUnit){
        enemySpawnManager.startSpawning(initialDelay, period, timeUnit);
    }

    public void startUpdatingEnemyPos(long initialDelay, long period, TimeUnit timeUnit){
        enemyUpdateManager.startUpdating(initialDelay, period, timeUnit);
    }

    public void startDispensingPowerUps(long initialDelay, long period, TimeUnit timeUnit) {
        powerUpManager.startDispensing(initialDelay, period, timeUnit);
    }
}
