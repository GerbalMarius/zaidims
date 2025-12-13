package org.game.client.components;

public interface ChatService {
    void sendMessage(String message);
    void addMessage(String playerName, String message);
}