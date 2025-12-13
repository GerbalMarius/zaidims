package org.game.entity.iterator;

import org.game.entity.Enemy;

import java.util.*;

public class EnemyIterator implements EntityIterator<Enemy> {
    private final Iterator<Enemy> iterator;

    public EnemyIterator(Map<Long, Enemy> enemies) {
        this.iterator = new ArrayList<>(enemies.values()).iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Enemy next() {
        return iterator.next();
    }
}