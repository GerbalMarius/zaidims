package org.game.message;

public record EnemyMoveMessage(long enemyId, int newX, int newY) implements Message {
}
