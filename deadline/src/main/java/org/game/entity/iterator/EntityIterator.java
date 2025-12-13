package org.game.entity.iterator;

public interface EntityIterator<T> {
    boolean hasNext();
    T next();
}
