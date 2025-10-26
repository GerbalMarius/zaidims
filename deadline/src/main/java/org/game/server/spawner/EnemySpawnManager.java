package org.game.server.spawner;

import lombok.extern.slf4j.Slf4j;
import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.entity.enemy.creator.EnemyCreator;
import org.game.server.Server;
import org.game.tiles.TileManager;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class EnemySpawnManager {

    private static final int ENEMY_SPAWN_POOL_SIZE = 2;

    private final Server server;
    private final Random random = new Random();

    private static long enemyId = 0;

    private final EnemySpawner goblinSpawner = new GoblinSpawner();
    private final EnemySpawner zombieSpawner = new ZombieSpawner();
    private final EnemySpawner skeletonSpawner = new SkeletonSpawner();

    private final Enemy goblinPrototype = EnemyCreator.createDefaultGoblin();
    private final Enemy zombiePrototype = EnemyCreator.createDefaultZombie();
    private final Enemy skeletonPrototype = EnemyCreator.createDefaultSkeleton();


    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(ENEMY_SPAWN_POOL_SIZE);

    public EnemySpawnManager(Server server) {
        this.server = server;
    }

    public void startSpawning(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(this::spawnRandomEnemy, initialDelay, period, timeUnit);
    }

    private void spawnRandomEnemy() {
        var enemyTypes = EnemyType.values();
        EnemySpawner spawner = switch (enemyTypes[random.nextInt(enemyTypes.length)]) {
            case GOBLIN -> goblinSpawner;
            case ZOMBIE -> zombieSpawner;
            case SKELETON -> skeletonSpawner;
        };

        var enemySizes = EnemySize.values();
        EnemySize size = enemySizes[random.nextInt(enemySizes.length)];

        TileManager tileManager = server.getEntityChecker().getTileManager();

        int[] pos = tileManager.findRandomSpawnPosition(random, 50);

        long nextId = enemyId++;

        int spawnX = pos[0];
        int spawnY = pos[1];
        Enemy enemy = switch (size) {
            case SMALL -> spawner.spawnSmall(nextId, spawnX, spawnY);
            case MEDIUM -> spawner.spawnMedium(nextId, spawnX, spawnY);
            case BIG -> spawner.spawnLarge(nextId, spawnX, spawnY);
        };

        Server.ServerActions.spawnEnemy(server, enemy, spawnX, spawnY);
    }

    public void startWaveSpawning(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(this::spawnWave, initialDelay, period, timeUnit);
    }

    private void spawnWave() {
        int waveSize = 5;
        TileManager tileManager = server.getEntityChecker().getTileManager();

        for (int i = 0; i < waveSize; i++) {
            Enemy prototype = chooseRandomPrototype();
            Enemy enemy = (Enemy) prototype.createDeepCopy();

            enemy.setId(enemyId++);
            int[] spawnPos = tileManager.findRandomSpawnPosition(random, 50);
            enemy.setGlobalX(spawnPos[0]);
            enemy.setGlobalY(spawnPos[1]);


            server.getEnemies().put(enemy.getId(), enemy);
            Server.ServerActions.spawnEnemy(server, enemy, spawnPos[0], spawnPos[1]);
        }
    }

    private Enemy chooseRandomPrototype() {
        return switch (EnemyType.values()[random.nextInt(EnemyType.values().length)]) {
            case GOBLIN -> goblinPrototype;
            case ZOMBIE -> zombiePrototype;
            case SKELETON -> skeletonPrototype;
        };
    }
}
