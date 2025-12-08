package org.game.client.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class KeyboardHandler extends KeyAdapter implements InputHandler {

    private boolean upPressed;

    private boolean downPressed;

    private boolean leftPressed;

    private boolean rightPressed;
    private boolean savePressed; // F5
    private boolean loadPressed; // F9


    @Override
    public void keyPressed(KeyEvent e) {
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
