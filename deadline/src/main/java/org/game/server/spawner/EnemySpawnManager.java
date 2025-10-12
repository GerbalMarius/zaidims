package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.entity.enemy.goblin.MediumGoblin;
import org.game.entity.enemy.skeleton.MediumSkeleton;
import org.game.entity.enemy.zombie.MediumZombie;
import org.game.server.Server;
import org.game.server.Server.ServerActions;
import org.game.server.WorldSettings;
import org.game.tiles.TileManager;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class EnemySpawnManager {

    private final Server server;
    private final Random random = new Random();

    private static long enemyId = 0;

    private final EnemySpawner goblinSpawner = new GoblinSpawner();
    private final EnemySpawner zombieSpawner = new ZombieSpawner();
    private final EnemySpawner skeletonSpawner = new SkeletonSpawner();

    private final Enemy goblinPrototype = new MediumGoblin(20, 20);
    private final Enemy zombiePrototype = new MediumZombie(20, 20);
    private final Enemy skeletonPrototype = new MediumSkeleton(20, 20);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

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

        int[] pos = findSpawnPosition();
        if (pos == null) {
            System.out.println("❌ Unable to find spawn position");
            return;
        }

        Enemy enemy = switch (size) {
            case SMALL -> spawner.spawnSmall(pos[0], pos[1]);
            case MEDIUM -> spawner.spawnMedium(pos[0], pos[1]);
            case BIG -> spawner.spawnLarge(pos[0], pos[1]);
        };

        enemy.setId(enemyId++);
        Server.ServerActions.spawnEnemy(server, enemy, pos[0], pos[1]);
    }

    public void startWaveSpawning(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(this::spawnWave, initialDelay, period, timeUnit);
    }

    private void spawnWave() {
        int waveSize = 5;

        for (int i = 0; i < waveSize; i++) {
            Enemy prototype = chooseRandomPrototype();
            Enemy enemy = (Enemy) prototype.createDeepCopy();

            enemy.setId(enemyId++);
            int[] spawnPos = findSpawnPosition();
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

    private int[] findSpawnPosition() {
        int maxAttempts = 50;
        for (int i = 0; i < maxAttempts; i++) {
            int x = random.nextInt(server.getEnemyChecker().getTileManager().getMapTileNum()[0].length * 32);
            int y = random.nextInt(server.getEnemyChecker().getTileManager().getMapTileNum().length * 32);

            if (isWalkableTile(x, y)) {
                return new int[]{x, y};
            }
        }
        return new int[]{0, 0}; // fallback
    }


    private boolean isWalkableTile(int x, int y) {
        TileManager tileManager = server.getEnemyChecker().getTileManager();

        int tileSize = WorldSettings.TILE_SIZE;
        int col = x / tileSize;
        int row = y / tileSize;

        int tileNum = tileManager.getMapTileNum()[row][col];
        return !tileManager.getTiles().get(tileNum).hasCollision();
    }
}
