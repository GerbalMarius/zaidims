package org.game.client.entity;

public enum ClassType {
    WARRIOR, WIZARD, ROGUE;

    public String getClassPrefix() {
       return switch (this) {
            case WIZARD -> "wiz";
            case ROGUE -> "rog";
            case WARRIOR -> "war";
        };
    }
}
