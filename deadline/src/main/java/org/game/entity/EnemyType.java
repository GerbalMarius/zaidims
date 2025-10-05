package org.game.entity;

public enum EnemyType {
    ZOMBIE,
    SKELETON,
    GOBLIN;

    public String getTypePrefix() {
        return switch (this) {
            case ZOMBIE -> "zom";
            case SKELETON -> "ske";
            case GOBLIN-> "gob";
        };
    }
}
