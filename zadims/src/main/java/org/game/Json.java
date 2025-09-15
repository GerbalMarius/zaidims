package org.game;

import com.google.gson.Gson;

public final class Json {
    private static Gson instance;
    private Json() {}

    public static synchronized Gson getInstance() {
        if (instance == null) {
            instance = new Gson();
        }
        return instance;
    }
}
