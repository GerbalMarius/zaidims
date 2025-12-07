package org.game.client.mediator;

import org.game.entity.ClassType;

public interface GameView {

    void onLocalPlayerJoined(ClassType playerClass, int x, int y);

    void onLocalPlayerMoveFromServer(int x, int y);

    void onLocalPlayerRespawn(int respawnX, int respawnY);
}

