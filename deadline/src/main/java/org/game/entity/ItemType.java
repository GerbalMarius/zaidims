package org.game.entity;

public enum ItemType {
    HEALTH_POTION("health_potion"),
    ATTACK_POTION("attack_potion"),
    SPEED_POTION("speed_potion");

    private final String fileName;

    ItemType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}