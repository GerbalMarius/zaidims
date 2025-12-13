package org.game.entity.iterator;

import org.game.entity.Projectile;

import java.util.*;

public class ProjectileIterator implements EntityIterator<Projectile> {
    private final Iterator<Projectile> iterator;

    public ProjectileIterator(Map<UUID, Projectile> projectiles) {
        this.iterator = new ArrayList<>(projectiles.values()).iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Projectile next() {
        return iterator.next();
    }
}