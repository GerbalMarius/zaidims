package org.game.message;

import java.util.UUID;

public record LeaveMessage(UUID playerId) implements Message {

}
