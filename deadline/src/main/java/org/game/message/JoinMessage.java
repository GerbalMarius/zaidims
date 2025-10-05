package org.game.message;

import org.game.entity.ClassType;

import java.util.UUID;

public record JoinMessage(UUID playerId, ClassType playerClass, String playerName,
                          int startPosX, int startPosY) implements Message{


    public JoinMessage(UUID playerId, ClassType playerClass ,String playerName) {
        this(playerId, playerClass, playerName, -1, -1);
    }

}
