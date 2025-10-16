package org.game.message;

import org.game.entity.powerup.PowerUpType;

public record PowerUpSpawnMessage(long powerUpId, PowerUpType powerUp, int x, int y) implements Message {
}
