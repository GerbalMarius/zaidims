package org.game.server.factory;

import org.game.entity.Item;
import org.game.entity.ItemType;

public class HealthPotionFactory implements ItemFactory {
    @Override
    public Item createItem(int x, int y) {
        return new Item(ItemType.HEALTH_POTION, x, y);
    }
}