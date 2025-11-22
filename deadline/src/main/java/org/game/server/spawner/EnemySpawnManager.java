package org.game.server.spawner;

import lombok.extern.slf4j.Slf4j;
import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.entity.enemy.creator.EnemyCreator;
import org.game.entity.enemy.wave.*;
import org.game.server.Server;
import org.game.server.Server.ServerActions;
import org.game.tiles.TileManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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

    private static int waveNumber = 1;

    private final EnemySpawner goblinSpawner = new GoblinSpawner();
    private final EnemySpawner zombieSpawner = new ZombieSpawner();
    private final EnemySpawner skeletonSpawner = new SkeletonSpawner();

    private final Enemy goblinPrototype = EnemyCreator.createDefaultGoblin();
    private final Enemy zombiePrototype = EnemyCreator.createDefaultZombie();
    private final Enemy skeletonPrototype = EnemyCreator.createDefaultSkeleton();

    private final List<WaveDefinition> waves = initWaves();


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
        TileManager tileManager = server.getEntityChecker().getTileManager();

        int index = Math.min(waveNumber - 1, waves.size() - 1);
        WaveDefinition definition = waves.get(index);

        log.debug("Spawning wave #{} with {} enemies",
                waveNumber, definition.size());

        definition.spawn(server, tileManager, enemyId, random);

        waveNumber++;
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
        } catch (Exception ex) {
            log.error("Spawn loop error", ex); // keep the task alive
        }
    }

    private List<WaveDefinition> initWaves() {
        int max = 20;
        List<WaveDefinition> waves = new ArrayList<>(max);

        final int baseWaveSize = 2;
        final int maxWaveSize = 30;

        for (int wave = 1; wave <= max; wave++) {

            int waveSize = scaleWaveSize(baseWaveSize, wave, maxWaveSize);

            double hpMul = Math.pow(1.18, wave - 1);
            double damageMul = Math.pow(1.15, wave - 1);

            int hpGrowthPercent = (int) Math.round(hpMul * 100.0);
            int damageGrowthPercent = (int) Math.round(damageMul * 100.0);

            WaveEntry root = buildWaveGroupForWave(
                    wave,
                    waveSize,
                    hpGrowthPercent,
                    damageGrowthPercent
            );

            waves.add(new WaveDefinition(wave, root));
        }

        return waves;
    }

    private WaveEntry buildWaveGroupForWave(int wave, int waveSize, int hpGrowthPercent, int damageGrowthPercent) {

        EnemyGroup root = new EnemyGroup();

        // 1–3: very simple waves, just random singles
        if (wave <= 3) {
            for (int i = 0; i < waveSize; i++) {
                Enemy prototype = chooseRandomPrototype();
                root.addChildEntry(
                        new SingleEnemySpawn(prototype, hpGrowthPercent, damageGrowthPercent)
                );
            }
            return root;
        }

        if (wave % 5 == 0) {
            Enemy boss = skeletonPrototype;
            root.addChildEntry(
                    new SquadGroup(
                            boss,     // leader
                            goblinPrototype,       // followers
                            5, 60,                 // count, spacing
                            hpGrowthPercent,
                            damageGrowthPercent
                    )
            );

            return root;
        }

        // 4–7: some lines, rest singles
        if (wave <= 7) {
            int lineCount = 2;
            int perLine = Math.max(3, waveSize / (lineCount + 1));

            for (int i = 0; i < lineCount; i++) {
                Enemy proto = (i % 2 == 0) ? zombiePrototype : goblinPrototype;
                root.addChildEntry(
                        new LineFormation(proto, perLine, 60,
                                hpGrowthPercent, damageGrowthPercent)
                );
            }

            int alreadyUsed = lineCount * perLine;
            int remaining = Math.max(0, waveSize - alreadyUsed);

            for (int i = 0; i < remaining; i++) {
                Enemy prototype = chooseRandomPrototype();
                root.addChildEntry(
                        new SingleEnemySpawn(prototype, hpGrowthPercent, damageGrowthPercent)
                );
            }

        }
        else {
            // Front line of zombies
            root.addChildEntry(
                    new LineFormation(zombiePrototype,
                            Math.max(4, waveSize / 3),
                            50,
                            hpGrowthPercent,
                            damageGrowthPercent)
            );

            // Backline circle of skeletons
            root.addChildEntry(
                    new CircleFormation(skeletonPrototype,
                            Math.max(3, waveSize / 4),
                            90,
                            hpGrowthPercent,
                            damageGrowthPercent)
            );

            // Fill the rest with random singles
            int used = Math.max(4, waveSize / 3) + Math.max(3, waveSize / 4);
            int remaining = Math.max(0, waveSize - used);

            for (int i = 0; i < remaining; i++) {
                Enemy prototype = chooseRandomPrototype();
                root.addChildEntry(
                        new SingleEnemySpawn(prototype, hpGrowthPercent, damageGrowthPercent)
                );
            }

        }
        return root;
    }

    private int scaleWaveSize(int base, int waveNumber, int cap) {
        double linear = base * (1.0 + 0.10 * (waveNumber - 1));
        int stepBonus = ((waveNumber - 1) / 5) * 2;
        int size = (int) Math.round(linear) + stepBonus;
        return Math.min(size, cap);
    }

}

