package org.game.server;

public interface Prototype extends Cloneable {
    Prototype createShallowCopy();

    Prototype createDeepCopy();
}
