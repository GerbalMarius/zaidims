package org.game.message;

import org.game.json.Json;

public record JoinMessage(String playerId, String playerName,
                          int startPosX, int startPosY) implements Message{


    public JoinMessage(String playerId, String playerName) {
        this(playerId, playerName, -1, -1);
    }

}
