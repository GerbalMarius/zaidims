package org.game.message;

public record EnemyMoveMessage(int enemyId, int newX, int newY) implements Message {
}
