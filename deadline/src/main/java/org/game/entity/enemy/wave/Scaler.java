package org.game.entity.enemy.wave;

import org.game.entity.Enemy;

import java.util.concurrent.atomic.AtomicLong;

class Scaler {
    private Scaler() {
    }

    static Enemy scaleEnemy(AtomicLong enemyId, Enemy prototype, int hpGrowth, int damageGrowth) {
        Enemy enemy = (Enemy) prototype.createDeepCopy();

        int hp = enemy.getMaxHitPoints() * hpGrowth / 100;
        enemy.setMaxHitPoints(hp);
        enemy.setHitPoints(hp);

        int attack = enemy.getAttack() * damageGrowth / 100;
        enemy.setAttack(attack);

        enemy.setId(enemyId.getAndIncrement());

        return enemy;
    }
}
