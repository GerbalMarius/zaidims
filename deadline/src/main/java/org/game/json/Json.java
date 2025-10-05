package org.game.json;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.game.message.*;

public final class Json {
    private final Gson gson;


    public Json() {
        RuntimeTypeAdapterFactory<Message> messageFactory = getMessageFactory();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(messageFactory);
        gson = gsonBuilder.create();
    }

    //load polymorphic types through these factories
    private static RuntimeTypeAdapterFactory<Message> getMessageFactory() {
        return RuntimeTypeAdapterFactory.of(Message.class, Message.JSON_LABEL)
                .registerSubtype(JoinMessage.class, "join")
                .registerSubtype(LeaveMessage.class, "leave")
                .registerSubtype(MoveMessage.class, "move")
                .registerSubtype(EnemyMoveMessage.class, "enemyMove")
                .registerSubtype(EnemySpawnMessage.class, "enemySpawn")
                .registerSubtype(EnemyRemoveMessage.class, "enemyRemove");

    }


    public <T, V> String toJson(T item, JsonLabelPair<V> prop1) {
        JsonObject jsonObj = gson.toJsonTree(item).getAsJsonObject();

        var val = prop1.value();
        var key = prop1.key();

        addPropertyToObj(jsonObj, key, val);
        return gson.toJson(jsonObj);
    }


    @SafeVarargs
    public final <T, V> String toJson(T item, JsonLabelPair<V>... props) {
        JsonObject jsonObj = gson.toJsonTree(item).getAsJsonObject();

        for (JsonLabelPair<V> prop : props) {
            var val = prop.value();
            var key = prop.key();
            addPropertyToObj(jsonObj, key, val);
        }
        return gson.toJson(jsonObj);
    }

    private <V> void addPropertyToObj(JsonObject jsonObj, String key, V val) {
        switch (val) {
            case JsonElement je -> jsonObj.add(key, je);
            case Number n -> jsonObj.addProperty(key, n);
            case Boolean b -> jsonObj.addProperty(key, b);
            case Character c -> jsonObj.addProperty(key, c.toString());
            case String s -> jsonObj.addProperty(key, s);
            case null, default -> {
                JsonElement generated = gson.toJsonTree(val);
                jsonObj.add(key, generated);
            }
        }
    }

    public <T> T fromJson(String json, Class<? extends T> clazz) {
        return gson.fromJson(json, clazz);
    }

}



