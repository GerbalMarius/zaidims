package org.game.entity.decorator;

import org.game.entity.Player;

public final class AttackDecorator extends PlayerDecorator {

    private final int bonusAttack;

    public AttackDecorator(Player wrappedPlayer, int bonusAttack) {
        super(wrappedPlayer);
        this.bonusAttack = bonusAttack;
    }

    @Override
    public int getAttack() { return super.getAttack() + bonusAttack; }
}