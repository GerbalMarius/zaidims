package org.game.client.input;

import org.game.client.Client;

public class RealNameService implements NameService {
    private final Client client;

    public RealNameService(Client client) {
        this.client = client;
    }

    @Override
    public void submitName(String name) {
        client.setPlayerName(name);
    }
}