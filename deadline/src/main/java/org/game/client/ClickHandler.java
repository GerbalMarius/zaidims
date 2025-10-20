package org.game.client;

public interface ClickHandler {
    default boolean isLeftClicked() { return false; }
    default boolean isRightClicked() { return false; }
}
