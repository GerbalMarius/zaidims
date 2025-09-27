package org.game.client;

import org.game.client.entity.ClassType;
import org.game.client.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GameState {

    private final Map<UUID, Player> players = new ConcurrentHashMap<>();

    public void addPlayer(UUID id, ClassType type, String name, int startingX, int startingY) {
        players.putIfAbsent(id, new Player(type, name, startingX, startingY));
    }

    public Player getPlayer(UUID id) {
        return players.get(id);
    }

    public void removePlayer(UUID id) {
        players.remove(id);
    }

    public Set<Map.Entry<UUID, Player>> getPlayerEntries()  {
        return Set.copyOf(players.entrySet());
    }
}
