package org.game.server.spawner;

import lombok.extern.slf4j.Slf4j;
import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.entity.enemy.creator.EnemyCreator;
import org.game.server.Server;
import org.game.server.Server.ServerActions;
import org.game.server.WorldSettings;
import org.game.tiles.TileManager;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public final class EnemySpawnManager {

    private static final int ENEMY_SPAWN_POOL_SIZE = 2;

    private final Server server;
    private final Random random = new Random();

    private final static AtomicLong enemyId = new AtomicLong(0);

    private static   int waveNumber = 1;

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
        scheduler.scheduleAtFixedRate(() -> runWithErrorLogger(this::spawnRandomEnemy), initialDelay, period, timeUnit);
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

        long nextId = enemyId.getAndIncrement();

        Enemy enemy = switch (size) {
            case SMALL -> spawner.spawnSmall(nextId, x, y);
            case MEDIUM -> spawner.spawnMedium(nextId, x, y);
            case BIG -> spawner.spawnLarge(nextId, x, y);
        };

        ServerActions.spawnEnemy(server, enemy, x, y);
    }

    public void startWaveSpawning(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(() -> runWithErrorLogger(this::spawnWave), initialDelay, period, timeUnit);
    }

    private void spawnWave() {
        final int baseWaveSize = 5;
        final int maxWaveSize = 30;

        TileManager tileManager = server.getEntityChecker().getTileManager();

        int waveSize = scaleWaveSize(baseWaveSize, waveNumber, maxWaveSize);
        log.debug("Spawning wave #{} with {} enemies", waveNumber, waveSize);

        for (int i = 0; i < waveSize; i++) {
            Enemy prototype = chooseRandomPrototype();
            Enemy enemy = (Enemy) prototype.createDeepCopy();

            scaleEnemyForWave(enemy, waveNumber);

            enemy.setId(enemyId.getAndIncrement());
            Point spawnPos = tileManager.findRandomSpawnPosition(random, 50);
            int x = spawnPos.x;
            int y = spawnPos.y;

            enemy.setGlobalX(x);
            enemy.setGlobalY(y);

            ServerActions.spawnEnemy(server, enemy, x, y);
        }
        waveNumber++;
    }

    private void scaleEnemyForWave(Enemy enemy, int waveNumber) {
        double hpMul = Math.pow(1.18, waveNumber - 1);
        double damageMul = Math.pow(1.15, waveNumber - 1);

        int hp = (int) Math.round(enemy.getMaxHitPoints() * hpMul);
        int attack = (int) Math.round(enemy.getAttack() * damageMul);

        enemy.setMaxHitPoints(hp);
        enemy.setHitPoints(hp);

        enemy.setAttack(attack);
    }

    private int scaleWaveSize(int base, int waveNumber, int cap) {
        double linear = base * (1.0 + 0.15 * (waveNumber - 1));
        int stepBonus = ((waveNumber - 1) / 5) * 2;
        int size = (int)Math.round(linear) + stepBonus;
        return Math.min(size, cap);
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

        Point spawnPos = tileManager.findRandomSpawnPosition(random, 50);
        int x = spawnPos.x;
        int y = spawnPos.y;

        Enemy mainEnemy = spawner.spawnLarge(enemyId.get(),x, y);
        mainEnemy.setId(enemyId.incrementAndGet());
        ServerActions.spawnEnemy(server, mainEnemy, x, y);

        int[][] offsets = {
                {100, 0},
                {-100, 0},
                {0, 100}
        };

        for (int[] offset : offsets) {
            int copyX = x + offset[0];
            int copyY = y + offset[1];

            int tileSize = WorldSettings.TILE_SIZE;
            int tileCol = copyX / tileSize;
            int tileRow = copyY / tileSize;

            if (!tileManager.IsWalkable(tileRow, tileCol)) {
                System.out.println("Kopijos vieta buvo neleistina");
                continue;
            }

            Enemy shallowCopy = (Enemy) mainEnemy.createShallowCopy();
            shallowCopy.setGlobalX(copyX);
            shallowCopy.setGlobalY(copyY);
            shallowCopy.setId(enemyId.incrementAndGet());

            ServerActions.spawnEnemy(server, shallowCopy, copyX, copyY);
        }

        System.out.println("Spawned shallow wave: main + 3 copies (" + mainEnemy.getType() + ")");
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

    private void runWithErrorLogger(Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            log.error("Spawn loop error", t); // keep the task alive
        }
    }
}
