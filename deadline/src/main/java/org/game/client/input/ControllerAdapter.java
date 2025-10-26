package org.game.client.input;

import java.util.Objects;

public final class ControllerAdapter implements InputHandler {
    private final Controller controller;

    public ControllerAdapter(Controller controller) {
        this.controller = Objects.requireNonNull(controller);
    }

    @Override
    public boolean isUpPressed() {
        return controller.isUp();
    }

    @Override
    public boolean isLeftPressed() {
        return controller.isLeft();
    }

    @Override
    public boolean isDownPressed() {
        return controller.isDown();
    }

    @Override
    public boolean isRightPressed() {
        return controller.isRight();
    }

    @Override
    public boolean isPrimaryClicked() {
        return controller.isButtonX();
    }

    @Override
    public boolean isSecondaryClicked() {
        return controller.isButtonY();
    }

    @Override
    public boolean isZPressed() {return  controller.isZPressed();}

    @Override
    public void update() {
        controller.update();
    }

    public void shutdown() {
        controller.shutdown();
    }
}
