package org.game.entity.attack;

import org.game.entity.FramePosition;
import org.game.entity.Player;
import org.game.entity.Projectile;

public class ProjectileAttack implements AttackBehavior {
    private final int speed;
    private final int damage;
    private final double maxDistance;
    private final long cooldownMs;

    public ProjectileAttack(int speed, int damage, double maxDistance, long cooldownMs) {
        this.speed = speed;
        this.damage = damage;
        this.maxDistance = maxDistance;
        this.cooldownMs = cooldownMs;
    }

    @Override
    public Projectile createProjectile(Player owner) {
        int px = owner.getGlobalX() + owner.getHitbox().x + owner.getHitbox().width / 2 - 8;
        int py = owner.getGlobalY() + owner.getHitbox().y + owner.getHitbox().height / 2 - 8;
        FramePosition dir = owner.getDirection();

        return new Projectile(px, py, dir, speed, damage, maxDistance);
    }

    @Override
    public long getCooldownMs() {
        return cooldownMs;
    }
}
