package org.game.client;

import org.game.client.input.ControllerAdapter;
import org.game.client.input.KeyboardHandler;
import org.game.client.input.MouseHandler;
import org.game.client.mediator.Mediator;
import org.game.entity.FramePosition;
import org.game.entity.Player;
import org.game.entity.command.Command;
import org.game.entity.command.MoveCommand;
import org.game.server.CollisionChecker;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public final class MovementCoordinator {

    private final UUID clientId;
    private final GameState state;

    private final KeyboardHandler keyboardHandler;
    private final MouseHandler mouseHandler;
    private final ControllerAdapter controllerAdapter;
    private final CollisionChecker collisionChecker;
    private final Mediator mediator;

    private final Deque<Command> history = new ArrayDeque<>();

    private int pendingDx = 0;
    private int pendingDy = 0;
    private long lastSendTime = 0;

    private int serverKnownX = 0;
    private int serverKnownY = 0;

    public MovementCoordinator(UUID clientId,
                               GameState state,
                               KeyboardHandler keyboardHandler,
                               MouseHandler mouseHandler,
                               ControllerAdapter controllerAdapter,
                               CollisionChecker collisionChecker,
                               Mediator mediator) {
        this.clientId = clientId;
        this.state = state;
        this.keyboardHandler = keyboardHandler;
        this.mouseHandler = mouseHandler;
        this.controllerAdapter = controllerAdapter;
        this.collisionChecker = collisionChecker;
        this.mediator = mediator;
    }
    public void update(long nowMillis, boolean hasFocus) {
        Player player = state.getPlayer(clientId);
        if (player == null || player.isDead() || !hasFocus) {
            return;
        }

        if (handleUndoIfRequested(player, nowMillis)) {
            return;
        }

        handleMovementInput(player);

        sendBatchIfDue(nowMillis);
    }

    /**
     * Called by GameView callbacks when server authoritative position changes.
     */
    public void updateServerKnownPosition(int x, int y) {
        this.serverKnownX = x;
        this.serverKnownY = y;
    }

    // --------------------------------------------------------------------
    // internal helpers
    // --------------------------------------------------------------------

    private boolean handleUndoIfRequested(Player player, long nowMillis) {
        if (!(mouseHandler.isSecondaryClicked() || controllerAdapter.isSecondaryClicked())
                || history.isEmpty()) {
            return false;
        }

        int undoCount = 3;
        for (int i = 0; i < undoCount && !history.isEmpty(); i++) {
            Command last = history.pop();
            last.undo();
        }

        int currentX = player.getGlobalX();
        int currentY = player.getGlobalY();

        int deltaFromServerX = currentX - serverKnownX;
        int deltaFromServerY = currentY - serverKnownY;

        if (deltaFromServerX != 0 || deltaFromServerY != 0) {
            mediator.onPlayerMove(deltaFromServerX, deltaFromServerY);
            serverKnownX = currentX;
            serverKnownY = currentY;
            lastSendTime = nowMillis;
        }

        pendingDx = 0;
        pendingDy = 0;

        return true; // we handled input this frame already
    }

    private void handleMovementInput(Player player) {
        int dx = 0, dy = 0;
        int speed = player.getSpeed();

        if (keyboardHandler.isLeftPressed() || controllerAdapter.isLeftPressed()) {
            dx -= speed;
        }
        if (keyboardHandler.isRightPressed() || controllerAdapter.isRightPressed()) {
            dx += speed;
        }
        if (keyboardHandler.isUpPressed() || controllerAdapter.isUpPressed()) {
            dy -= speed;
        }
        if (keyboardHandler.isDownPressed() || controllerAdapter.isDownPressed()) {
            dy += speed;
        }

        if (dx != 0) {
            moveHorizontally(player, dx);
        }
        if (dy != 0) {
            moveVertically(player, dy);
        }
    }

    private void moveHorizontally(Player player, int dx) {
        player.setCollisionOn(false);
        player.setDirection(dx > 0 ? FramePosition.RIGHT : FramePosition.LEFT);
        collisionChecker.checkTile(player);
        if (!player.isCollisionOn()) {
            MoveCommand moveCmd = new MoveCommand(
                    player, player.getGlobalX() + dx, player.getGlobalY()
            );
            moveCmd.execute();
            history.push(moveCmd);
            pendingDx += dx;
        }
    }

    private void moveVertically(Player player, int dy) {
        player.setCollisionOn(false);
        player.setDirection(dy > 0 ? FramePosition.DOWN : FramePosition.UP);
        collisionChecker.checkTile(player);
        if (!player.isCollisionOn()) {
            MoveCommand moveCmd = new MoveCommand(
                    player, player.getGlobalX(), player.getGlobalY() + dy
            );
            moveCmd.execute();
            history.push(moveCmd);
            pendingDy += dy;
        }
    }

    private void sendBatchIfDue(long nowMillis) {
        if (nowMillis - lastSendTime <= 50) {
            return;
        }
        if (pendingDx == 0 && pendingDy == 0) {
            lastSendTime = nowMillis;
            return;
        }

        mediator.onPlayerMove(pendingDx, pendingDy);
        pendingDx = 0;
        pendingDy = 0;
        lastSendTime = nowMillis;
    }
}