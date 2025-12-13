package org.game.client.components;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChatUI {
    private boolean chatOpen = false;
    private StringBuilder currentMessage = new StringBuilder();
    private List<ChatEntry> chatHistory = new ArrayList<>();
    private static final int MAX_MESSAGES = 10;
    private static final int MESSAGE_DISPLAY_TIME = 5000;

    public void toggleChat() {
        chatOpen = !chatOpen;
        if (!chatOpen && currentMessage.length() > 0) {
            currentMessage.setLength(0);
        }
    }

    public void addCharacter(char c) {
        if (chatOpen && currentMessage.length() < 100) {
            currentMessage.append(c);
        }
    }

    public void removeCharacter() {
        if (chatOpen && currentMessage.length() > 0) {
            currentMessage.deleteCharAt(currentMessage.length() - 1);
        }
    }

    public String getCurrentMessage() {
        return currentMessage.toString();
    }

    public void clearCurrentMessage() {
        currentMessage.setLength(0);
    }

    public void addMessage(String playerName, String message) {
        chatHistory.add(new ChatEntry(playerName, message, System.currentTimeMillis()));
        if (chatHistory.size() > MAX_MESSAGES) {
            chatHistory.remove(0);
        }
    }

    public boolean isChatOpen() {
        return chatOpen;
    }

    public void render(Graphics2D g2, int screenWidth, int screenHeight) {
        long currentTime = System.currentTimeMillis();
        chatHistory.removeIf(entry -> !chatOpen && currentTime - entry.timestamp > MESSAGE_DISPLAY_TIME);

        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        int yOffset = screenHeight - 150;

        for (int i = Math.max(0, chatHistory.size() - 5); i < chatHistory.size(); i++) {
            ChatEntry entry = chatHistory.get(i);
            String text = entry.playerName + ": " + entry.message;

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(10, yOffset - 15, textWidth + 10, 20);

            g2.setColor(Color.WHITE);
            g2.drawString(text, 15, yOffset);
            yOffset += 25;
        }

        if (chatOpen) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(10, screenHeight - 50, 400, 30);
            g2.setColor(Color.WHITE);
            g2.drawRect(10, screenHeight - 50, 400, 30);
            g2.drawString("Chat: " + currentMessage.toString() + "_", 15, screenHeight - 30);
        }
    }

    private record ChatEntry(String playerName, String message, long timestamp) {}
}
