package org.game.message;

import org.game.entity.ItemType;
public record ItemCollectMessage(long itemId) implements Message {}