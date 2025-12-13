package org.game.message;

public record ChatMessage(String playerName, String message, long timestamp) implements Message {
    public ChatMessage(String playerName, String message) {
        this(playerName, message, System.currentTimeMillis());
    }
}
