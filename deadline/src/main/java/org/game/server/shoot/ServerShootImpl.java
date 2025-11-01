package org.game.server.shoot;

import org.game.client.shoot.ShootImplementation;
import org.game.entity.Player;
import org.game.entity.Projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ServerShootImpl implements ShootImplementation {

    @Override
    public List<ProjectileData> createProjectiles(Player player, UUID baseProjectileId, long timestamp) {
        List<ProjectileData> result = new ArrayList<>();

        Projectile proj = player.tryAttack(timestamp);
        if (proj != null) {
            result.add(new ProjectileData(baseProjectileId, proj));
        }

        return result;
    }
}