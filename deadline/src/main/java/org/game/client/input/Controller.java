package org.game.client.input;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;

@Slf4j
@Getter
public final class Controller {

    private final int controllerIndex;
    private final float deadZone;

    private boolean connected = false;
    private boolean up, down, left, right;
    private boolean buttonX, buttonY;

    public Controller() {
        this(0, 0.25f);
    }

    public Controller(int controllerIndex, float deadZone) {
        this.controllerIndex = Math.max(0, Math.min(15, controllerIndex));
        this.deadZone = Math.max(0f, Math.min(1f, deadZone));

        if (!GLFW.glfwInit()) {
            log.error("Failed to initialize GLFW");
        }
    }

    public void update() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GLFWGamepadState state = GLFWGamepadState.malloc(stack);

            int jid = GLFW.GLFW_JOYSTICK_1 + controllerIndex;

            if (!GLFW.glfwJoystickPresent(jid) || !GLFW.glfwJoystickIsGamepad(jid)) {
                connected = false;
                clear();
                return;
            }

            if (!GLFW.glfwGetGamepadState(jid, state)) {
                connected = false;
                clear();
                return;
            }

            connected = true;

            // Buttons
            this.buttonX = state.buttons(GLFW.GLFW_GAMEPAD_BUTTON_X) == GLFW.GLFW_PRESS;
            this.buttonY = state.buttons(GLFW.GLFW_GAMEPAD_BUTTON_Y) == GLFW.GLFW_PRESS;

            // D-Pad
            this.up    = state.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP)    == GLFW.GLFW_PRESS;
            this.down  = state.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN)  == GLFW.GLFW_PRESS;
            this.left  = state.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT)  == GLFW.GLFW_PRESS;
            this.right = state.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) == GLFW.GLFW_PRESS;

            // Left stick axes
            float lx = state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);
            float ly = state.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);

            this.left  = this.left  || lx < -deadZone;
            this.right = this.right || lx >  deadZone;
            this.up    = this.up    || ly < -deadZone;
            this.down  = this.down  || ly >  deadZone;

        } catch (Exception ex) {
            log.error("Controller update error: {}", ex.getMessage());
            clear();
        }
    }

    private void clear() {
        up = down = left = right = false;
        buttonX = buttonY = false;
    }
}