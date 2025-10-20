package org.game.client;

public interface InputHandler extends ClickHandler {
    // directions
    boolean isUpPressed();
    boolean isDownPressed();
    boolean isLeftPressed();
    boolean isRightPressed();

    default void update() {}
}