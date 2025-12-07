package org.game.server.Admin;

import org.game.server.GameWorldFacade;
import org.game.server.Server;

public class InterpreterContext {
    private final GameWorldFacade gameWorld;
    private final Server server;

    public InterpreterContext(Server server) {
        this.server = server;
        this.gameWorld = server.getGameWorld();
    }

    public GameWorldFacade getGameWorld() {
        return gameWorld;
    }

    public Server getServer() {
        return server;
    }
}
