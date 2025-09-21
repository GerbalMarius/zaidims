package org.game.client;

import org.game.client.entity.Player;

import java.util.*;

public final class GameState {

    private final Map<UUID, Player> players = new HashMap<>();

    public void addPlayer(UUID id, String name, int startingX, int startingY) {
        players.putIfAbsent(id, new Player(name, startingX, startingY));
    }

    public Player getPlayer(UUID id) {
        return players.get(id);
    }

    public void removePlayer(UUID id) {
        players.remove(id);
    }

    public Set<Map.Entry<UUID, Player>> getPlayerEntries(){
        return Collections.unmodifiableSet(players.entrySet());
    }
}
