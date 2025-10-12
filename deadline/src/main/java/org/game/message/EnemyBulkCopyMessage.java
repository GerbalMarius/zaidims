package org.game.message;

import java.util.Map;


public record EnemyBulkCopyMessage(Map<Long, EnemyCopy> enemies) implements Message {

    public EnemyBulkCopyMessage {
        enemies = Map.copyOf(enemies);
    }
}
