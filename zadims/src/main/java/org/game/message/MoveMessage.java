package org.game.message;

public record MoveMessage(MessageType type, String playerId, int newX, int newY) {


    public MoveMessage {
        if(type != MessageType.MOVE) {
            throw new IllegalArgumentException("type must be Move");
        }
    }

    public MoveMessage(String playerId, int newX,  int newY) {
        this(MessageType.MOVE , playerId, newX, newY);
    }
}
