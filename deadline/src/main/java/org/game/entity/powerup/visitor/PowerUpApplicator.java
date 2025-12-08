package org.game.entity.powerup.visitor;

import lombok.Getter;
import org.game.entity.Player;
import org.game.entity.decorator.AttackDecorator;
import org.game.entity.decorator.MaxHpDecorator;
import org.game.entity.decorator.SpeedDecorator;
import org.game.entity.powerup.*;

public class PowerUpApplicator implements PowerUpVisitor {

    @Getter
    private Player resultingPlayer;

    public PowerUpApplicator(Player originalPlayer) {this.resultingPlayer = originalPlayer;}

    @Override
    public void visit(AttackPowerUp p) {this.resultingPlayer = new AttackDecorator(resultingPlayer, p.getFlatAttackIncrease());}

    @Override
    public void visit(SpeedPowerUp p) {this.resultingPlayer = new SpeedDecorator(resultingPlayer, p.getFlatSpeedIncrease());}

    @Override
    public void visit(MaxHpPowerUp p) {this.resultingPlayer = new MaxHpDecorator(resultingPlayer, p.getFlatHpIncrease());}

    @Override
    public void visit(ArmorPowerUp p) {p.applyTo(resultingPlayer);}

    @Override
    public void visit(ShieldPowerUp p) {p.applyTo(resultingPlayer);}
}
