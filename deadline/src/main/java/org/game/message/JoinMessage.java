package org.game.message;

import org.game.json.Json;

import java.util.UUID;

public record JoinMessage(UUID playerId, String playerName,
                          int startPosX, int startPosY) implements Message{


    public JoinMessage(UUID playerId, String playerName) {
        this(playerId, playerName, -1, -1);
    }

}
