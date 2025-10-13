package org.game.message;

import java.util.UUID;

public record PlayerHealthUpdateMessage(UUID playerId, int newHealth) implements Message {
}
