package org.game.entity.attack;

import org.game.entity.Player;
import org.game.entity.Projectile;

public interface AttackBehavior {
    Projectile createProjectile(Player owner);
    long getCooldownMs();
}
