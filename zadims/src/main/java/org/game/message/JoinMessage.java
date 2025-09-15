package org.game.message;

public record JoinMessage(MessageType type, String playerId, String playerName,
                          int startPosX, int startPosY){

    public JoinMessage {
        if(type != MessageType.JOIN) {
            throw new IllegalArgumentException("type must be JOIN");
        }
    }

    public JoinMessage(String playerId, String playerName) {
        this(MessageType.JOIN , playerId, playerName, -1, -1);
    }
}
