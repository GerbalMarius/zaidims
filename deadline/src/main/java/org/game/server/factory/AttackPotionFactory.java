package org.game.server.factory;

import org.game.entity.Item;
import org.game.entity.ItemType;

public class AttackPotionFactory implements ItemFactory {
    @Override
    public Item createItem(int x, int y) {
        return new Item(ItemType.ATTACK_POTION, x, y);
    }
}