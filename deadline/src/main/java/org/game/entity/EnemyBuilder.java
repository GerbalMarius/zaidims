package org.game.entity;

import org.game.server.spawner.EnemySpawner;
import org.game.server.spawner.GoblinSpawner;
import org.game.server.spawner.SkeletonSpawner;
import org.game.server.spawner.ZombieSpawner;

public class EnemyBuilder {
    private EnemyType type = EnemyType.ZOMBIE;
    private EnemySize size = EnemySize.MEDIUM;
    private int x;
    private int y;
    private long id = -1;
    private Integer customHitPoints;
    private Integer customAttack;
    private Integer customSpeed;
    private EnemySpawner spawner;

    public EnemyBuilder ofType(EnemyType type) {
        this.type = type;
        if (this.spawner == null) {
            this.spawner = getSpawnerForType(type);
        }
        return this;
    }

    public EnemyBuilder withSpawner(EnemySpawner spawner) {
        this.spawner = spawner;
        return this;
    }

    public EnemyBuilder withSize(EnemySize size) {
        this.size = size;
        return this;
    }

    public EnemyBuilder at(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public EnemyBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public EnemyBuilder withHitPoints(int hitPoints) {
        this.customHitPoints = hitPoints;
        return this;
    }

    public EnemyBuilder withAttack(int attack) {
        this.customAttack = attack;
        return this;
    }

    public EnemyBuilder withSpeed(int speed) {
        this.customSpeed = speed;
        return this;
    }

    public Enemy build() {
        if (spawner == null) {
            spawner = getSpawnerForType(type);
        }

        Enemy enemy = createEnemy();

        if (customHitPoints != null) {
            enemy.setHitPoints(customHitPoints);
            enemy.setMaxHitPoints(customHitPoints);
        }

        if (customAttack != null) {
            enemy.setAttack(customAttack);
        }

        if (customSpeed != null) {
            enemy.setSpeed(customSpeed);
        }

        if (id != -1) {
            enemy.setId(id);
        }

        return enemy;
    }

    private Enemy createEnemy() {
        return switch (size) {
            case SMALL -> spawner.spawnSmall(x, y);
            case MEDIUM -> spawner.spawnMedium(x, y);
            case BIG -> spawner.spawnLarge(x, y);
        };
    }

    private EnemySpawner getSpawnerForType(EnemyType type) {
        return switch (type) {
            case ZOMBIE -> new ZombieSpawner();
            case SKELETON -> new SkeletonSpawner();
            case GOBLIN -> new GoblinSpawner();
        };
    }
}
