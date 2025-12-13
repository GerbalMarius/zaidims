package org.game.message;

public sealed interface Message
        permits ChatMessage, EnemyBulkCopyMessage, EnemyHealthUpdateMessage, EnemyMoveMessage, EnemyRemoveMessage, EnemySpawnMessage, JoinMessage, LeaveMessage, MoveMessage, PlayerDefenseUpdateMessage, PlayerHealthUpdateMessage, PlayerRespawnMessage, PlayerStatsUpdateMessage, PowerUpRemoveMessage, PowerUpSpawnMessage, ProjectileSpawnMessage {


    String JSON_LABEL = "type";

}
