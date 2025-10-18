package org.game.message;

import java.util.UUID;

public record PlayerStatsUpdateMessage(UUID playerId, int hitPoints, int maxHitPoints, int attack, int speed) implements Message {
}
