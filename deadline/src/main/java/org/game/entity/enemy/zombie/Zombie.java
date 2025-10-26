package org.game.entity.enemy.zombie;

import lombok.extern.slf4j.Slf4j;
import org.game.entity.Enemy;
import org.game.entity.EnemyType;
import org.game.entity.enemy.builder.AbstractEnemyBuilder;
import org.game.entity.enemy.builder.EnemyBuilder;

import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public abstract class Zombie extends Enemy {

    protected Zombie(int x, int y) {
        super(x, y);

        loadSprite("zom", "enemy");
        this.type = EnemyType.ZOMBIE;
    }

    public static EnemyBuilder<Zombie> builder() {
        return new ZombieBuilder();
    }


    public static class ZombieBuilder extends AbstractEnemyBuilder<Zombie> {

        private Supplier<? extends Zombie> seed;

        private  ZombieBuilder() {}

        @Override
        public EnemyBuilder<Zombie> withEnemy(Supplier<? extends Zombie> enemySeed) {
            this.seed = Objects.requireNonNull(enemySeed);
            return this;
        }

        @Override
        protected Zombie initializeEnemy() {
            return seed.get();
        }
    }
}

