package org.game.entity.enemy.skeleton;

import lombok.extern.slf4j.Slf4j;
import org.game.entity.Enemy;
import org.game.entity.EnemyType;
import org.game.entity.enemy.builder.AbstractEnemyBuilder;
import org.game.entity.enemy.builder.EnemyBuilder;
import org.game.entity.enemy.template.SkeletonAI;

import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public abstract class Skeleton extends Enemy {

    protected Skeleton(int x, int y) {
        super(x, y);

        loadSprite("ske", "enemy");
        this.type = EnemyType.SKELETON;

        this.ai = new SkeletonAI();
    }

    public static EnemyBuilder<Skeleton> builder() {
        return new SkeletonBuilder();
    }

    public static class SkeletonBuilder extends AbstractEnemyBuilder<Skeleton> {

        private Supplier<? extends Skeleton> seed;

        private  SkeletonBuilder() {}

        @Override
        public EnemyBuilder<Skeleton> withEnemy(Supplier<? extends Skeleton> enemySeed) {
            this.seed = Objects.requireNonNull(enemySeed);
            return this;
        }

        @Override
        protected Skeleton initializeEnemy() {
            return seed.get();
        }
    }
    
}
