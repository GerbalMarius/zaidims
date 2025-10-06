package org.game.server.factory;

import org.game.entity.Item;

public interface ItemFactory {
    Item createItem(int x, int y);
}