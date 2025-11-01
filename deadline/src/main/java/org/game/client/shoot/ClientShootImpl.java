package org.game.client.shoot;

import org.game.client.GameState;
import org.game.entity.Player;
import org.game.entity.Projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ClientShootImpl implements ShootImplementation {
    private final GameState state;

    public ClientShootImpl(GameState state) {
        this.state = state;
    }

    @Override
    public List<ProjectileData> createProjectiles(Player player, UUID baseProjectileId, long timestamp) {
        List<ProjectileData> result = new ArrayList<>();

        Projectile proj = player.getAttackBehavior().createProjectile(player);
        if (proj != null) {
            state.getProjectiles().put(baseProjectileId, proj);
            result.add(new ProjectileData(baseProjectileId, proj));
        }

        return result;
    }

}
