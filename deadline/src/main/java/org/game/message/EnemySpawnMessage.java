package org.game.message;

import org.game.entity.EnemySize;
import org.game.entity.EnemyType;

public record EnemySpawnMessage(long enemyId, EnemyType enemyType, EnemySize size, int initialX, int initialY) implements Message {
}
