package org.game.entity.powerup.visitor;
import org.game.entity.powerup.*;


public interface PowerUpVisitor {
    void visit(AttackPowerUp powerUp);
    void visit(SpeedPowerUp powerUp);
    void visit(MaxHpPowerUp powerUp);
    void visit(ArmorPowerUp powerUp);
    void visit(ShieldPowerUp powerUp);
}