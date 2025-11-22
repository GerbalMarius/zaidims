package org.game.entity.enemy.goblin;

import lombok.extern.slf4j.Slf4j;
import org.game.entity.Enemy;
import org.game.entity.EnemyType;
import org.game.entity.enemy.builder.AbstractEnemyBuilder;
import org.game.entity.enemy.builder.EnemyBuilder;
import org.game.entity.enemy.template.GoblinAI;

import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public abstract class Goblin extends Enemy {

    protected Goblin(int x, int y) {
        super(x, y);

        loadSprite("gob", "enemy");
        this.type = EnemyType.GOBLIN;

        this.ai = new GoblinAI();
    }

    public static EnemyBuilder<Goblin> builder() {
        return new GoblinBuilder();
    }

    public static class GoblinBuilder extends AbstractEnemyBuilder<Goblin> {

        private Supplier<? extends Goblin> seed;

        private GoblinBuilder() {}

        @Override
        public EnemyBuilder<Goblin> withEnemy(Supplier<? extends Goblin> enemySeed) {
            this.seed = Objects.requireNonNull(enemySeed);
            return this;
        }

        @Override
        protected Goblin initializeEnemy() {
            return seed.get();
        }
    }

}
