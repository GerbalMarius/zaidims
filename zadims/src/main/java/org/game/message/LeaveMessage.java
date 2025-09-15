package org.game.message;

public record LeaveMessage(MessageType type, String playerId) {

    public LeaveMessage {
        if(type != MessageType.LEAVE) {
            throw new IllegalArgumentException("type must be Leave");
        }
    }

    public LeaveMessage(String playerId) {
        this(MessageType.LEAVE, playerId);
    }
}
