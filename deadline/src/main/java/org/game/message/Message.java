package org.game.message;

public sealed interface Message
        permits EnemyBulkCopyMessage, EnemyHealthUpdateMessage, EnemyMoveMessage, EnemyRemoveMessage, EnemySpawnMessage, JoinMessage, LeaveMessage, MoveMessage, ProjectileSpawnMessage {

    String JSON_LABEL = "type";

}
