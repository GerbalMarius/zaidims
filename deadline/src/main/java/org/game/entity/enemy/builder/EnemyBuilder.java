package org.game.entity.enemy.builder;

import org.game.entity.Enemy;

import java.util.function.Supplier;

public interface EnemyBuilder<T extends Enemy> {

    T build();

    EnemyBuilder<T> atPos(int x, int y);

    EnemyBuilder<T> withEnemy(Supplier<? extends T> enemySeed);

    EnemyBuilder<T> withHp(int hp);

    EnemyBuilder<T> withAttack(int attack);

    EnemyBuilder<T> withSpeed(int speed);

    EnemyBuilder<T> withId(long id);


}
