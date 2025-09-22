package org.game.client.entity;

import lombok.Getter;

import java.util.Objects;


@Getter
public final class Player extends Entity {

    private final String name;


    public Player(String name, int x, int y) {
        Objects.requireNonNull(name);
        super(x, y);
        this.name = name;
    }

}
