package org.game.entity.enemy.creator;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.server.spawner.EnemySpawner;
import org.game.server.spawner.GoblinSpawner;
import org.game.server.spawner.SkeletonSpawner;
import org.game.server.spawner.ZombieSpawner;

import java.util.EnumMap;
import java.util.Map;

/**
 * A utility class responsible for creating and spawning enemies based on their type and size from server messages.
 * This class uses a registry to associate an {@link EnemyType} with its corresponding {@link EnemySpawner}.
 * It supports the creation of enemies of varying sizes (SMALL, MEDIUM, BIG).
 * Instances of the created enemies are initialized with specific attributes as defined by the
 * respective spawner implementations.
 * This class is immutable and thread-safe as its state is confined to a final {@code Map}
 * and no external modification is allowed after construction.
 */
public final class EnemyMessageCreator {


    private final Map<EnemyType, EnemySpawner> registry = new EnumMap<>(EnemyType.class);

    public EnemyMessageCreator() {
        registry.put(EnemyType.ZOMBIE,   new ZombieSpawner());
        registry.put(EnemyType.SKELETON, new SkeletonSpawner());
        registry.put(EnemyType.GOBLIN,   new GoblinSpawner());
    }

    public Enemy spawn(EnemyType type, EnemySize size, long id, int x, int y) {
        EnemySpawner spawner = registry.get(type);

        return switch (size) {
            case SMALL  -> spawner.spawnSmall(id, x, y);
            case MEDIUM -> spawner.spawnMedium(id, x, y);
            case BIG    -> spawner.spawnLarge(id, x, y);
        };
    }
}