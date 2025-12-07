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
        RuntimeTypeAdapterFactory<Message> parent = RuntimeTypeAdapterFactory.of(Message.class, Message.JSON_LABEL);

        RuntimeTypeAdapterFactory<Message> coreMessages = parent
                .registerSubtype(JoinMessage.class, "join")
                .registerSubtype(LeaveMessage.class, "leave")
                .registerSubtype(MoveMessage.class, "move");

        RuntimeTypeAdapterFactory<Message> enemyMessages = coreMessages
                .registerSubtype(EnemyMoveMessage.class, "enemyMove")
                .registerSubtype(EnemySpawnMessage.class, "enemySpawn")
                .registerSubtype(EnemyRemoveMessage.class, "enemyRemove")
                .registerSubtype(EnemyBulkCopyMessage.class, "enemyCopy")
                .registerSubtype(EnemyHealthUpdateMessage.class, "enemyHealth");

        RuntimeTypeAdapterFactory<Message> playerMessages = enemyMessages
                .registerSubtype(ProjectileSpawnMessage.class, "projectileSpawn")
                .registerSubtype(PlayerHealthUpdateMessage.class, "playerHealth")
                .registerSubtype(PlayerRespawnMessage.class, "playerRespawn");

        return playerMessages
                .registerSubtype(PowerUpSpawnMessage.class, "powerUpSpawn")
                .registerSubtype(PowerUpRemoveMessage.class, "powerUpRemove")
                .registerSubtype(PlayerStatsUpdateMessage.class, "playerStats")
                .registerSubtype(PlayerDefenseUpdateMessage.class, "playerDefense");
    }


    public <T, V> String toJson(T item, JsonLabelPair<V> prop1) {
        JsonObject jsonObj = gson.toJsonTree(item).getAsJsonObject();

        var val = prop1.value();
        var key = prop1.key();

        addPropertyToObj(jsonObj, key, val);
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



