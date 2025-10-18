package org.game.entity.decorator;

import org.game.entity.Player;

public abstract class PlayerDecorator extends Player {

    protected final Player wrappedPlayer;

    protected PlayerDecorator(Player wrappedPlayer) {
        this.wrappedPlayer = wrappedPlayer;
        super(wrappedPlayer.getPlayerClass(), wrappedPlayer.getName(), wrappedPlayer.getGlobalX(), wrappedPlayer.getGlobalY());
    }


    @Override
    public int getAttack() {
        return wrappedPlayer.getAttack();
    }

    @Override
    public int getSpeed() {
        return wrappedPlayer.getSpeed();
    }



    @Override
    public int getHitPoints() {
        return wrappedPlayer.getHitPoints();
    }

    @Override
    public void setHitPoints(int hp) {
        wrappedPlayer.setHitPoints(hp);
    }

    @Override
    public int getMaxHitPoints() {
        return wrappedPlayer.getMaxHitPoints();
    }

    @Override
    public void setMaxHitPoints(int hp) {
        wrappedPlayer.setMaxHitPoints(hp);
    }


    @Override
    public void setHpRegenAmount(int hpRegenAmount) {
        wrappedPlayer.setHpRegenAmount(hpRegenAmount);
    }
    @Override
    public int getHpRegenAmount() {
        return wrappedPlayer.getHpRegenAmount();
    }

    @Override
    public long getHpRegenIntervalMs() {
        return wrappedPlayer.getHpRegenIntervalMs();
    }

}