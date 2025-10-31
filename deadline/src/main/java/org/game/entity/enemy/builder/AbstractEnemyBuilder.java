package org.game.entity.enemy.builder;


import org.game.entity.Enemy;

public abstract class AbstractEnemyBuilder<T extends Enemy> implements EnemyBuilder<T> {

    protected T enemy;
    protected int x, y;
    protected int hp;
    protected int attack;
    protected int speed;
    protected long id;
    

    @Override
    public EnemyBuilder<T> atPos(int x, int y) {
        this.x = x; this.y = y;
        return this;
    }

    @Override
    public EnemyBuilder<T> withHp(int hp) {
        if (hp <= 0) throw new IllegalArgumentException("Health must be positive");
        this.hp = hp;
        return this;
    }

    @Override
    public EnemyBuilder<T> withAttack(int attack) {
        if (attack <= 0) throw new IllegalArgumentException("Attack must be positive");
        this.attack = attack;
        return this;
    }

    @Override
    public EnemyBuilder<T> withSpeed(int speed) {
        if (speed <= 0) throw new IllegalArgumentException("Speed must be positive");
        this.speed = speed;
        return this;
    }

    @Override
    public EnemyBuilder<T> withId(long id) {
        if (id < 0) throw new IllegalArgumentException("Id must be non-negative");
        this.id = id;
        return this;
    }

    protected abstract T initializeEnemy();

    protected void applyCommonFields(T enemy) {
        enemy.setGlobalX(x);
        enemy.setGlobalY(y);
        enemy.setTargetX(x);
        enemy.setTargetY(y);
        enemy.setPrevX(x);
        enemy.setPrevY(y);

        enemy.setId(id);

        enemy.setSpeed(speed);
        enemy.setAttack(attack);
        enemy.setHitPoints(hp);
        enemy.setMaxHitPoints(hp);

        enemy.setLastUpdateTime(System.currentTimeMillis());
    }

    @Override
    public T build() {
        T createdEnemy = this.enemy != null ? this.enemy : initializeEnemy();
        applyCommonFields(createdEnemy);
        return createdEnemy;
    }
}
