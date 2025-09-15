package org.game.json;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.game.message.JoinMessage;
import org.game.message.LeaveMessage;
import org.game.message.Message;
import org.game.message.MoveMessage;

public final class Json {
    private final Gson gson;


    public Json() {
        RuntimeTypeAdapterFactory<Message> messageFactory = getMessageFactory();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(messageFactory);
        gson = gsonBuilder.create();
    }

    private static RuntimeTypeAdapterFactory<Message> getMessageFactory() {
        return RuntimeTypeAdapterFactory.of(Message.class, Message.JSON_LABEL)
                .registerSubtype(JoinMessage.class, "join")
                .registerSubtype(LeaveMessage.class,  "leave")
                .registerSubtype(MoveMessage.class,   "move");
    }

    @SafeVarargs
    public final <T, V> String toJson(T item, JsonLabelPair<V>... labels) {
        JsonObject jsonObj = gson.toJsonTree(item).getAsJsonObject();

        for (JsonLabelPair<V> label : labels) {
            var val = label.value();
            var key = label.name();
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
        return gson.toJson(jsonObj);
    }



    private String returnWithProperty(JsonObject jsonObject ,String property, String value) {
        jsonObject.addProperty(property, value);
        return jsonObject.toString();
    }

    public <T> T fromJson(String json, Class<? extends T> clazz) {
        return gson.fromJson(json, clazz);
    }

}
