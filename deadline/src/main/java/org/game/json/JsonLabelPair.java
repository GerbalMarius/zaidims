package org.game.json;

public record JsonLabelPair<T>(String name, T value) {

    public static <T> JsonLabelPair<T> labelPair(String name, T value) {
        return new JsonLabelPair<>(name, value);
    }
}
