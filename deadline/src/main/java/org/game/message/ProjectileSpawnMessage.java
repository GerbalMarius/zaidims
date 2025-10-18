package org.game.message;

import org.game.entity.FramePosition;

import java.util.UUID;

public record ProjectileSpawnMessage(int startX, int startY, FramePosition direction, UUID projectileId, UUID playerId) implements Message {

}