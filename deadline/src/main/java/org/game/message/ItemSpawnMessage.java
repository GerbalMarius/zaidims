package org.game.message;

import org.game.entity.ItemType;

public record ItemSpawnMessage(
        long itemId,
        ItemType itemType,
        int x,
        int y
) implements Message {}

