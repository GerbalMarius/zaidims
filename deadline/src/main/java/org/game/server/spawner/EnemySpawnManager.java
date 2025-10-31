package org.game.server.spawner;

import lombok.extern.slf4j.Slf4j;
import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.entity.enemy.creator.EnemyCreator;
import org.game.server.Server;
import org.game.server.WorldSettings;
import org.game.tiles.TileManager;

import java.awt.*;
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

        Point spawnPos = tileManager.findRandomSpawnPosition(random, 50);
        final int x = spawnPos.x;
        final int y = spawnPos.y;

        long nextId = enemyId++;



        Enemy enemy = switch (size) {
            case SMALL -> spawner.spawnSmall(nextId, x, y);
            case MEDIUM -> spawner.spawnMedium(nextId, x, y);
            case BIG -> spawner.spawnLarge(nextId, x, y);
        };

        Server.ServerActions.spawnEnemy(server, enemy, x, y);
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
            Point spawnPos = tileManager.findRandomSpawnPosition(random, 50);
            int x = spawnPos.x;
            int y = spawnPos.y;

            enemy.setGlobalX(x);
            enemy.setGlobalY(y);


            server.getEnemies().put(enemy.getId(), enemy);
            Server.ServerActions.spawnEnemy(server, enemy, x, y);
        }
    }

    public void startShallowWaveSpawning(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(this::spawnShallowWave, initialDelay, period, timeUnit);
        comparePrototypeCopies();
    }

    public void spawnShallowWave() {
        TileManager tileManager = server.getEntityChecker().getTileManager();

        EnemySpawner spawner = switch (EnemyType.values()[random.nextInt(EnemyType.values().length)]) {
            case GOBLIN -> goblinSpawner;
            case ZOMBIE -> zombieSpawner;
            case SKELETON -> skeletonSpawner;
        };

        int[] mainPos = tileManager.findRandomSpawnPosition(random, 50);

        // sukuriamas pgr priesas
        Enemy mainEnemy = spawner.spawnLarge(mainPos[0], mainPos[1]);
        mainEnemy.setId(enemyId++);
        server.getEnemies().put(mainEnemy.getId(), mainEnemy);
        Server.ServerActions.spawnEnemy(server, mainEnemy, mainPos[0], mainPos[1]);

        // Aplink ji sukuriam 3 shallow kopijas
        int[][] offsets = {
                {100, 0},
                {-100, 0},
                {0, 100}
        };

        for (int[] offset : offsets) {
            int copyX = mainPos[0] + offset[0];
            int copyY = mainPos[1] + offset[1];

            int tileSize = WorldSettings.TILE_SIZE;
            int tileCol = copyX / tileSize;
            int tileRow = copyY / tileSize;

            if (!tileManager.isWalkable(tileRow, tileCol)) {
                System.out.println("Kopijos vieta buvo neleistina");
                continue;
            }

            Enemy shallowCopy = (Enemy) mainEnemy.createShallowCopy();
            shallowCopy.setGlobalX(copyX);
            shallowCopy.setGlobalY(copyY);
            shallowCopy.setId(enemyId++);

            server.getEnemies().put(shallowCopy.getId(), shallowCopy);
            Server.ServerActions.spawnEnemy(server, shallowCopy, copyX, copyY);
        }

        System.out.println("💀 Spawned shallow wave: main + 3 copies (" + mainEnemy.getType() + ")");
    }

    public void comparePrototypeCopies() {
        System.out.println("--- Prototype Copy Comparison --");

        Enemy prototype = skeletonPrototype;
        System.out.println("Original Enemy: " + prototype + " @ " + System.identityHashCode(prototype));

        Enemy shallowCopy = (Enemy) prototype.createShallowCopy();
        Enemy deepCopy = (Enemy) prototype.createDeepCopy();

        System.out.println("Shallow Copy:   " + shallowCopy + " @ " + System.identityHashCode(shallowCopy));
        System.out.println("Deep Copy:      " + deepCopy + " @ " + System.identityHashCode(deepCopy));

        System.out.println("--- Laukai (adresai) ---");
        System.out.println("Hitbox:");
        System.out.println("  original: " + System.identityHashCode(prototype.getHitbox()));
        System.out.println("  shallow:  " + System.identityHashCode(shallowCopy.getHitbox()));
        System.out.println("  deep:     " + System.identityHashCode(deepCopy.getHitbox()));
    }

    private Enemy chooseRandomPrototype() {
        return switch (EnemyType.values()[random.nextInt(EnemyType.values().length)]) {
            case GOBLIN -> goblinPrototype;
            case ZOMBIE -> zombiePrototype;
            case SKELETON -> skeletonPrototype;
        };
    }
}
