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
        player.setGlobalX(newX);
        player.setGlobalY(newY);

        player.setPrevX(newX);
        player.setPrevY(newY);

        player.setTargetX(newX);
        player.setTargetY(newY);

    }

    @Override
    public void undo() {
        player.setGlobalX(prevX);
        player.setGlobalY(prevY);

        player.setPrevX(prevX);
        player.setPrevY(prevY);

        player.setTargetX(prevX);
        player.setTargetY(prevY);

    }
}

