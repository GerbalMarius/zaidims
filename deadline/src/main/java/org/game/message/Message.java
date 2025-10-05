package org.game.message;

public sealed interface Message
                        permits JoinMessage, LeaveMessage, MoveMessage,
                            EnemySpawnMessage, EnemyMoveMessage, EnemyRemoveMessage{

    String JSON_LABEL = "type";


}
