package org.game.json;

public record JsonLabelPair<T>(String key, T value) {

    public static <T> JsonLabelPair<T> labelPair(String key, T value) {
        return new JsonLabelPair<>(key, value);
    }
}
