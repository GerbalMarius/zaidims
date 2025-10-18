package org.game.entity.powerup;

import java.awt.*;

public interface PowerUp {
    long getId();
    void setId(long id);
    void draw(Graphics2D g2d);

    Rectangle getHitbox();

    PowerUpType getType();
}
