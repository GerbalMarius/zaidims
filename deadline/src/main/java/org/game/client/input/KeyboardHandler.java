package org.game.client.input;

import org.game.client.Client;
import org.game.client.components.ChatUI;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class KeyboardHandler extends KeyAdapter implements InputHandler {

    private final ChatUI chatUI;
    private final Client client;

    private boolean upPressed;

    private boolean downPressed;

    private boolean leftPressed;

    private boolean rightPressed;
    private boolean savePressed; // F5
    private boolean loadPressed; // F9

    public KeyboardHandler(ChatUI chatUI, Client client) {
        this.chatUI = chatUI;
        this.client = client;
    }

    private void resetMovementKeys() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_T && !chatUI.isChatOpen()) {
            chatUI.toggleChat();
            resetMovementKeys();
            return;
        }
        if (chatUI.isChatOpen()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER -> {
                    String message = chatUI.getCurrentMessage();
                    if (!message.trim().isEmpty()) {
                        client.sendChatMessage(message);
                    }
                    chatUI.clearCurrentMessage();
                    chatUI.toggleChat();
                    resetMovementKeys();
                }
                case KeyEvent.VK_ESCAPE -> {
                    chatUI.toggleChat();
                    resetMovementKeys();
                }
                case KeyEvent.VK_BACK_SPACE -> chatUI.removeCharacter();
            }
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_F5 -> savePressed = true;
            case KeyEvent.VK_F9 -> loadPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(chatUI.isChatOpen()) {return;}
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> upPressed = false;
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> downPressed = false;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_F5 -> savePressed = false;
            case KeyEvent.VK_F9 -> loadPressed = false;

        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
        if (chatUI.isChatOpen()) {
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c) || Character.isWhitespace(c) ||
                    "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) != -1) {
                chatUI.addCharacter(c);
            }
        }
    }

    @Override
    public boolean isUpPressed() {
        return upPressed;
    }

    @Override
    public boolean isDownPressed() {
        return downPressed;
    }

    @Override
    public boolean isLeftPressed() {
        return leftPressed;
    }

    @Override
    public boolean isRightPressed() {
        return rightPressed;
    }
    @Override
    public boolean isSavePressed() { return savePressed; }

    @Override
    public boolean isLoadPressed() { return loadPressed; }
}
