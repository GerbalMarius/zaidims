package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.server.Server;
import org.game.tiles.TileManager;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class EnemySpawnManager {

    private static final int ENEMY_SPAWN_POOL_SIZE = 2;

    private final Server server;
    private final Random random = new Random();
    private final int[] waveCounter = {0};
    private static long enemyId = 0;

    private final EnemySpawner goblinSpawner;
    private final EnemySpawner zombieSpawner;
    private final EnemySpawner skeletonSpawner;

//    private final Enemy goblinPrototype = new MediumGoblin(20, 20);
//    private final Enemy zombiePrototype = new MediumZombie(20, 20);
//    private final Enemy skeletonPrototype = new MediumSkeleton(20, 20);


    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(ENEMY_SPAWN_POOL_SIZE);

    public EnemySpawnManager(Server server) {
        this.server = server;
        this.goblinSpawner = new GoblinSpawner();
        this.zombieSpawner = new ZombieSpawner();
        this.skeletonSpawner = new SkeletonSpawner();
    }

    public void startSpawning(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(this::spawnRandomEnemy, initialDelay, period, timeUnit);
    }

    private void spawnRandomEnemy() {
        var enemyTypes = EnemyType.values();
        EnemyType type = enemyTypes[random.nextInt(enemyTypes.length)];

        var enemySizes = EnemySize.values();
        EnemySize size = enemySizes[random.nextInt(enemySizes.length)];

        TileManager tileManager = server.getEntityChecker().getTileManager();
        int[] pos = tileManager.findRandomSpawnPosition(random, 50);


        Enemy enemy = spawnFromSpawner(type,size,pos[0],pos[1]);

        enemy.setId(enemyId++);
        server.getEnemies().put(enemy.getId(),enemy);
        Server.ServerActions.spawnEnemy(server, enemy, pos[0], pos[1]);
    }

    public void startWaveSystem(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(() -> {
            waveCounter[0]++;
            spawnEnemyWave(waveCounter[0]);
        }, initialDelay, period, timeUnit);
    }

    private void spawnEnemyWave(int waveNumber) {
        int baseCount = 3 + waveNumber;
        boolean isBossWave = (waveNumber % 5 == 0);
        TileManager tileManager = server.getEntityChecker().getTileManager();

        for (int i = 0; i < baseCount; i++) {

            EnemyType type = getEnemyTypeForWave(waveNumber);
            EnemySize size = determineEnemySize(waveNumber);
            int[] spawnPos = tileManager.findRandomSpawnPosition(random, 50);

            int healthBonus = Math.max(0, (waveNumber - 3) * 10);
            int attackBonus = Math.max(0, (waveNumber - 3) * 2);

            Enemy enemy = spawnFromSpawner(type, size, spawnPos[0], spawnPos[1]);


            enemy.setHitPoints(getBaseHealth(size) + healthBonus);
            enemy.setMaxHitPoints(getBaseHealth(size) + healthBonus);
            enemy.setAttack(getBaseAttack(size) + attackBonus);

            enemy.setId(enemyId++);
            server.getEnemies().put(enemy.getId(), enemy);
            Server.ServerActions.spawnEnemy(server, enemy, spawnPos[0], spawnPos[1]);
        }

        if (isBossWave) {
            int[] centerPos = tileManager.findRandomSpawnPosition(random, 100);

            EnemyType bossType = EnemyType.values()[waveNumber % EnemyType.values().length];

            Enemy bossEnemy = spawnFromSpawner(bossType, EnemySize.BIG, centerPos[0], centerPos[1]);

            bossEnemy.setHitPoints(200 + (waveNumber * 20));
            bossEnemy.setMaxHitPoints(200 + (waveNumber * 20));
            bossEnemy.setAttack(30 + (waveNumber * 3));
            bossEnemy.setSpeed(3);

            bossEnemy.setId(enemyId++);
            server.getEnemies().put(bossEnemy.getId(), bossEnemy);
            Server.ServerActions.spawnEnemy(server, bossEnemy, centerPos[0], centerPos[1]);
        }
    }

    private Enemy spawnFromSpawner(EnemyType type, EnemySize size, int x, int y) {
        EnemySpawner spawner = switch (type) {
            case GOBLIN -> goblinSpawner;
            case ZOMBIE -> zombieSpawner;
            case SKELETON -> skeletonSpawner;
        };

        return switch (size) {
            case SMALL -> spawner.spawnSmall(x, y);
            case MEDIUM -> spawner.spawnMedium(x, y);
            case BIG -> spawner.spawnLarge(x, y);
        };
    }

    private EnemyType getEnemyTypeForWave(int waveNumber) {

        int roll = random.nextInt(100);

        if (waveNumber <= 3) {
            if (roll < 50) return EnemyType.GOBLIN;
            if (roll < 80) return EnemyType.ZOMBIE;
            return EnemyType.SKELETON;
        } else if (waveNumber <= 7) {
            if (roll < 30) return EnemyType.GOBLIN;
            if (roll < 70) return EnemyType.ZOMBIE;
            return EnemyType.SKELETON;
        } else {
            if (roll < 20) return EnemyType.GOBLIN;
            if (roll < 50) return EnemyType.ZOMBIE;
            return EnemyType.SKELETON;
        }
    }

    private EnemySize determineEnemySize(int waveNumber) {
        int roll = random.nextInt(100);
        if (waveNumber <= 3) {
            if (roll < 70) return EnemySize.SMALL;
            if (roll < 95) return EnemySize.MEDIUM;
            return EnemySize.BIG;
        } else if (waveNumber <= 7) {
            if (roll < 40) return EnemySize.SMALL;
            if (roll < 80) return EnemySize.MEDIUM;
            return EnemySize.BIG;
        } else {
            if (roll < 20) return EnemySize.SMALL;
            if (roll < 70) return EnemySize.MEDIUM;
            return EnemySize.BIG;
        }
    }

    private int getBaseHealth(EnemySize size) {
        return switch (size) {
            case SMALL -> 40;
            case MEDIUM -> 80;
            case BIG -> 120;
        };
    }

    private int getBaseAttack(EnemySize size) {
        return switch (size) {
            case SMALL -> 10;
            case MEDIUM -> 15;
            case BIG -> 25;
        };
    }

    public void startWaveSpawning(int i, int i1, TimeUnit timeUnit) {
    }

//    private Enemy chooseRandomPrototype() {
//        return switch (EnemyType.values()[random.nextInt(EnemyType.values().length)]) {
//            case GOBLIN -> goblinPrototype;
//            case ZOMBIE -> zombiePrototype;
//            case SKELETON -> skeletonPrototype;
//        };
//    }
}
