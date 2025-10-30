package org.game.entity.enemy.creator;

import org.game.entity.Enemy;
import org.game.entity.EnemySize;
import org.game.entity.EnemyType;
import org.game.server.spawner.EnemySpawner;
import org.game.server.spawner.GoblinSpawner;
import org.game.server.spawner.SkeletonSpawner;
import org.game.server.spawner.ZombieSpawner;

import java.util.Map;
public final class EnemyMessageCreator {

    private final Map<EnemyType, EnemySpawner> registry = Map.of(
            EnemyType.ZOMBIE,   new ZombieSpawner(),
            EnemyType.SKELETON, new SkeletonSpawner(),
            EnemyType.GOBLIN,   new GoblinSpawner()
    );

    public Enemy spawn(EnemyType type, EnemySize size, long id, int x, int y) {
        EnemySpawner spawner = registry.get(type);

        return switch (size) {
            case SMALL  -> spawner.spawnSmall(id, x, y);
            case MEDIUM -> spawner.spawnMedium(id, x, y);
            case BIG    -> spawner.spawnLarge(id, x, y);
        };
    }
}