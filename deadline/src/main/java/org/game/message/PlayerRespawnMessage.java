package org.game.message;

import java.util.UUID;

public record PlayerRespawnMessage(UUID playerId, int respawnX, int respawnY) implements Message {
}
