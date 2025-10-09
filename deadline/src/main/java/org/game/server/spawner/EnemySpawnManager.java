package org.game.server.spawner;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.server.Server;
import org.game.server.WorldSettings;
import org.game.tiles.TileManager;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class EnemySpawnManager {

    private final Server server;
    private final Random random = new Random();

    private final EnemySpawner goblinSpawner = new GoblinSpawner();
    private final EnemySpawner zombieSpawner = new ZombieSpawner();
    private final EnemySpawner skeletonSpawner = new SkeletonSpawner();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public EnemySpawnManager(Server server) {
        this.server = server;
    }

    public void startSpawning(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(this::spawnRandomEnemy, initialDelay, period, timeUnit);
    }

    private void spawnRandomEnemy() {

         var  enemyTypes = EnemyType.values();
        EnemySpawner spawner = switch (enemyTypes[random.nextInt(enemyTypes.length)]) {
            case GOBLIN -> goblinSpawner;
            case ZOMBIE -> zombieSpawner;
            case SKELETON -> skeletonSpawner;
        };


        EnemySize size = EnemySize.values()[random.nextInt(EnemySize.values().length)];


//        int x = random.nextInt(WorldSettings.WORLD_WIDTH / 2);
//        int y = random.nextInt(WorldSettings.WORLD_HEIGHT / 2);
        int maxAttempts = 50;
        int x = 0, y = 0;
        boolean found = false;

        for(int i = 0; i < maxAttempts; i++) {
            x = random.nextInt(WorldSettings.WORLD_WIDTH);
            y = random.nextInt(WorldSettings.WORLD_HEIGHT);

            if(isWalkableTile(x,y)) {
                found = true;
                break;
            }
        }

        if(!found) {
            System.out.println("unable to spawn");
        }


        Enemy enemy = switch (size) {
            case SMALL -> spawner.spawnSmall(x, y);
            case MEDIUM -> spawner.spawnMedium(x, y);
            case BIG -> spawner.spawnLarge(x, y);
        };

        Server.ServerActions.spawnEnemy(server, enemy, x, y);

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
