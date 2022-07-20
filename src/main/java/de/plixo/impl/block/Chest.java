package de.plixo.impl.block;

import de.plixo.game.ItemInventory;
import de.plixo.game.blocks.InventoryBlock;

public class Chest extends InventoryBlock {
    public Chest() {
        inventory = new ItemInventory(5,5);
    }
}
