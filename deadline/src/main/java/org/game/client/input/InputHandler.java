package org.game.client.input;

/**
 * Interface providing default input state methods for directional and click actions.
 * <p>
 * This interface defines methods that can be overridden to detect user input actions
 * such as pressing directional keys (up, down, left, right) or mouse clicks.
 * By default, all methods return {@code false}, meaning no input action is active,
 * and {@link #update()} does nothing.
 * </p>
 */
public interface InputHandler {

    /**
     * Indicates whether the 'UP' direction has been pressed or activated.
     *
     * @return {@code true} if the 'UP' direction is currently pressed, otherwise {@code false}.
     */
    default boolean isUpPressed() {
        return false;
    }

    /**
     * Indicates whether the 'DOWN' direction has been pressed or activated.
     *
     * @return {@code true} if the 'DOWN' direction is currently pressed, otherwise {@code false}.
     */
    default boolean isDownPressed() {
        return false;
    }

    /**
     * Indicates whether the 'LEFT' direction has been pressed or activated.
     *
     * @return {@code true} if the 'LEFT' direction is currently pressed, otherwise {@code false}.
     */
    default boolean isLeftPressed() {
        return false;
    }

    /**
     * Indicates whether the 'RIGHT' direction has been pressed or activated.
     *
     * @return {@code true} if the 'RIGHT' direction is currently pressed, otherwise {@code false}.
     */
    default boolean isRightPressed() {
        return false;
    }

    /**
     * Indicates whether the primary button has been clicked.
     *
     * @return {@code true} if the primary button has been clicked, otherwise {@code false}.
     */
    default boolean isPrimaryClicked() {
        return false;
    }

    /**
     * Indicates whether the secondary button has been clicked.
     *
     * @return {@code true} if the secondary button  has been clicked, otherwise {@code false}.
     */
    default boolean isSecondaryClicked() {
        return false;
    }

    /**
     * Updates the internal state of input detection.
     * <p>
     * Implementations should override this method to update
     * input state each frame or polling cycle when outside the AWT Event Queue and java.awt event namespace.
     * </p>
     */
    default void update() {}


    /**
     * Indicates whether *any* input event-related key or button is currently pressed.
     * <p>
     * This includes directional keys (up, down, left, right) as well as primary
     * and secondary mouse/controller buttons.
     *
     * @return {@code true} if any key or button is currently pressed; {@code false} otherwise.
     */

    default boolean isZPressed() { return false;}

    default boolean anyKeyPressed() {
        return isLeftPressed() || isUpPressed() || isRightPressed() || isDownPressed()
                || isPrimaryClicked() || isSecondaryClicked() || isZPressed();
    }

}
