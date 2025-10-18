package org.game.entity.decorator;

import org.game.entity.Player;

public final class MaxHpDecorator extends PlayerDecorator {

    private final int bonusHp;

    public MaxHpDecorator(Player wrappedPlayer, int bonusHp) {
        super(wrappedPlayer);
        this.bonusHp = bonusHp;
    }

    @Override
    public int getMaxHitPoints() { return super.getMaxHitPoints() +  bonusHp; }

    @Override
    public  int getHitPoints() { return super.getHitPoints(); }

    @Override
    public void setMaxHitPoints(int hp) {
        super.setMaxHitPoints(hp);
    }


}
