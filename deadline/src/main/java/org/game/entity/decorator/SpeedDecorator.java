package org.game.entity.decorator;

import org.game.entity.Player;

public final class SpeedDecorator extends PlayerDecorator {

    private final int bonusSpeed;

    public SpeedDecorator(Player wrappedPlayer, int bonusSpeed) {
        super(wrappedPlayer);
        this.bonusSpeed = bonusSpeed;
    }

    @Override
    public int getSpeed() {
        return super.getSpeed() + bonusSpeed;
    }
}
