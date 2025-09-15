package org.game.client;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameState {

    private final Map<String, PlayerInfo> players = new HashMap<>();

    public void addPlayer(String id, String name, int startingX, int startingY) {
        players.putIfAbsent(id, new PlayerInfo(name, new Point(startingX, startingY)));
    }

    public void setPlayerPosition(String id, int newX, int newY) {
        PlayerInfo playerInfo = players.computeIfAbsent(id, k -> new PlayerInfo("?", new Point(newX, newY)));

        playerInfo.coordinates().setLocation(newX, newY);
    }

    public void removePlayer(String id) {
        players.remove(id);
    }

    public synchronized boolean hasPlayer(String id) {
        return players.containsKey(id);
    }

    public synchronized void movePlayerBy(String id, int dx, int dy) {
        PlayerInfo info = players.get(id);
        if (info == null) return;

        Point coordinates = info.coordinates();
        coordinates.x += dx;
        coordinates.y += dy;
    }

    public Map<String, PlayerInfo> getPlayerViews(){
        return Collections.unmodifiableMap(players);
    }
}
