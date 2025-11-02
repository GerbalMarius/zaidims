package org.game.entity.command;

import lombok.Getter;
import org.game.entity.Player;

public class MoveCommand implements Command {

    private final Player player;
    @Getter
    private final int prevX;
    @Getter
    private final int prevY;
    @Getter
    private final int newX;
    @Getter
    private final int newY;

    public MoveCommand(Player player, int newX, int newY) {
        this.player = player;
        this.prevX = player.getGlobalX();
        this.prevY = player.getGlobalY();
        this.newX = newX;
        this.newY = newY;
    }

    @Override
    public void execute() {
       player.moveBy(newX - prevX, newY - prevY);

    }

    @Override
    public void undo() {
        player.moveBy(-(newX - prevX), -(newY - prevY));
    }
}