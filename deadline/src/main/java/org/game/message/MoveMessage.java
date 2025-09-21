package org.game.message;

import java.util.UUID;

public record MoveMessage(UUID playerId, int newX, int newY) implements Message {

}
