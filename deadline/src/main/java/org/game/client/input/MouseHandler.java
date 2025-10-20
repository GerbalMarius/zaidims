package org.game.client.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public final class MouseHandler extends MouseAdapter implements InputHandler {
    private boolean leftClicked;

    private boolean rightClicked;

    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1 -> leftClicked = true;
            case MouseEvent.BUTTON3 -> rightClicked = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (e.getButton()){
            case MouseEvent.BUTTON1 -> leftClicked = false;
            case MouseEvent.BUTTON3 -> rightClicked = false;
        }
    }

    @Override
    public boolean isPrimaryClicked() {
        return leftClicked;
    }

    @Override
    public boolean isSecondaryClicked() {
        return rightClicked;
    }
}
