package org.game.client;

import java.util.Objects;

public final class ControllerAdapter implements InputHandler {
    private final ControllerHandler controllerInputHandler;

    public ControllerAdapter(ControllerHandler controllerInputHandler) {
        this.controllerInputHandler = Objects.requireNonNull(controllerInputHandler);
    }

    @Override
    public boolean isUpPressed() {
        return controllerInputHandler.isUp();
    }

    @Override
    public boolean isLeftPressed() {
        return controllerInputHandler.isLeft();
    }

    @Override
    public boolean isDownPressed() {
        return controllerInputHandler.isDown();
    }

    @Override
    public boolean isRightPressed() {
        return controllerInputHandler.isRight();
    }

    @Override
    public boolean isLeftClicked() {
        return controllerInputHandler.isButtonX();
    }

    @Override
    public boolean isRightClicked() {
        return controllerInputHandler.isButtonY();
    }

    @Override
    public void update() {
        controllerInputHandler.update();
    }

    public void shutdown() {
        controllerInputHandler.shutdown();
    }
}
