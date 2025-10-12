package org.game.message;

public record EnemyHealthUpdateMessage(long enemyId, int newHitPoints) implements Message {

}
