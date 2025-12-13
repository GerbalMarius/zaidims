package org.game.client.components;

public class RealChatService implements ChatService {
    private final ChatUI chatUI;
    private final MessageSender messageSender;

    public RealChatService(ChatUI chatUI, MessageSender messageSender) {
        this.chatUI = chatUI;
        this.messageSender = messageSender;
    }

    @Override
    public void sendMessage(String message) {
        messageSender.send(message);
    }

    @Override
    public void addMessage(String playerName, String message) {
        chatUI.addMessage(playerName, message);
    }

    @FunctionalInterface
    public interface MessageSender {
        void send(String message);
    }
}