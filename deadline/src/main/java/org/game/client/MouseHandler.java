package org.game.client;

import lombok.Getter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


@Getter
public final class MouseHandler extends MouseAdapter {
    private boolean leftClicked;

    private boolean rightClicked;

    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1 -> leftClicked = true;
            case MouseEvent.BUTTON2 -> rightClicked = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (e.getButton()){
            case MouseEvent.BUTTON1 -> leftClicked = false;
            case MouseEvent.BUTTON2 -> rightClicked = false;
        }
    }

    public boolean anyButtonPressed() {
        return leftClicked || rightClicked;
    }
}
