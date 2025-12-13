package org.game.entity.iterator;

import org.game.entity.Player;

import java.util.*;

public class PlayerIterator implements EntityIterator<Player> {
    private final Iterator<Player> iterator;

    public PlayerIterator(Map<UUID, Player> players) {
        this.iterator = new ArrayList<>(players.values()).iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Player next() {
        return iterator.next();
    }
}