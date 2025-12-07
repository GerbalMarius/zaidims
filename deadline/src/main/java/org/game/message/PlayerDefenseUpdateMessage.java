package org.game.message;

import java.util.UUID;

public record PlayerDefenseUpdateMessage(
        UUID playerId,
        int armorCount,
        boolean isShieldActive
) implements Message {
}
