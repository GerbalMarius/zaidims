package org.game.message;

public record MoveMessage(String playerId, int newX, int newY) implements Message {

}
