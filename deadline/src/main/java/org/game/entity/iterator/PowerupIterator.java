package org.game.entity.iterator;

import org.game.entity.powerup.PowerUp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class PowerupIterator implements EntityIterator<PowerUp> {
    private final Iterator<PowerUp> iterator;

    public PowerupIterator(Map<Long, PowerUp> powerUps) {
        this.iterator = new ArrayList<>(powerUps.values()).iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public PowerUp next() {
        return iterator.next();
    }
}
