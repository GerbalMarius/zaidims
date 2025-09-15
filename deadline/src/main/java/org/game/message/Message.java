package org.game.message;

public sealed interface Message
                        permits JoinMessage, LeaveMessage, MoveMessage{

    String JSON_LABEL = "type";


}
