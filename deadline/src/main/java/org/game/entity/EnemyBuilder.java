package org.game.entity;

import org.game.entity.enemy.goblin.BigGoblin;
import org.game.entity.enemy.goblin.MediumGoblin;
import org.game.entity.enemy.goblin.SmallGoblin;
import org.game.entity.enemy.skeleton.BigSkeleton;
import org.game.entity.enemy.skeleton.MediumSkeleton;
import org.game.entity.enemy.skeleton.SmallSkeleton;
import org.game.entity.enemy.zombie.BigZombie;
import org.game.entity.enemy.zombie.MediumZombie;
import org.game.entity.enemy.zombie.SmallZombie;

public class EnemyBuilder {
    private EnemyType type = EnemyType.ZOMBIE;
    private EnemySize size = EnemySize.MEDIUM;
    private int x;
    private int y;
    private long id = -1;
    private Integer customHitPoints;
    private Integer customAttack;
    private Integer customSpeed;

    public EnemyBuilder ofType(EnemyType type) {
        this.type = type;
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
        return switch (type) {
            case ZOMBIE -> switch (size) {
                case SMALL -> new SmallZombie(x, y);
                case MEDIUM -> new MediumZombie(x, y);
                case BIG -> new BigZombie(x, y);
            };
            case SKELETON -> switch (size) {
                case SMALL -> new SmallSkeleton(x, y);
                case MEDIUM -> new MediumSkeleton(x, y);
                case BIG -> new BigSkeleton(x, y);
            };
            case GOBLIN -> switch (size) {
                case SMALL -> new SmallGoblin(x, y);
                case MEDIUM -> new MediumGoblin(x, y);
                case BIG -> new BigGoblin(x, y);
            };
        };
    }
    public EnemyBuilder copy() {
        EnemyBuilder b = new EnemyBuilder();
        b.type = this.type;
        b.size = this.size;
        b.x = this.x;
        b.y = this.y;
        b.id = this.id;
        b.customHitPoints = this.customHitPoints;
        b.customAttack = this.customAttack;
        b.customSpeed = this.customSpeed;
        return b;
    }
}
