package org.game.entity.decorator;

import org.game.entity.Player;
import org.game.entity.PlayerMemento;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class PlayerDecorator extends Player {

    protected final Player wrappedPlayer;

    protected PlayerDecorator(Player wrappedPlayer) {
        super(wrappedPlayer.getPlayerClass(), wrappedPlayer.getName(), wrappedPlayer.getGlobalX(), wrappedPlayer.getGlobalY(), wrappedPlayer.getDamageApplier());
        this.wrappedPlayer = wrappedPlayer;
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


    @Override
    public boolean isShieldActive() {
        return wrappedPlayer.isShieldActive();
    }

    @Override
    public void setShieldActive(boolean active) {
        wrappedPlayer.setShieldActive(active);
    }

    @Override
    public int getArmorCount() {
        return wrappedPlayer.getArmorCount();
    }

    @Override
    public void setArmorCount(int armorCount) {
        wrappedPlayer.setArmorCount(armorCount);
    }

    @Override
    public int getMaxArmorCount() {
        return wrappedPlayer.getMaxArmorCount();
    }

    @Override
    public void drawHealthAndArmorBar(Graphics2D g2, int x, int y, int width, Color hpColor) {
        wrappedPlayer.drawHealthAndArmorBar(g2, x, y, width, hpColor);
    }

    @Override
    public PlayerMemento createMemento() {
        return wrappedPlayer.createMemento();
    }

    @Override
    public void restoreMemento(PlayerMemento memento) {
        wrappedPlayer.restoreMemento(memento);
    }

}
