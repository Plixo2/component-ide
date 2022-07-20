package de.plixo.game;

import de.plixo.game.item.ItemStack;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public class ItemInventory {

    @Getter
    @Accessors(fluent = true)
    private ItemStack[][] items;

    @Getter
    @Accessors(fluent = true)
    private int sizeX, sizeY;

    public ItemInventory(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.items = new ItemStack[sizeY][sizeX];
    }

    private void tryMerge(@NotNull ItemStack stack) {
        final var copy = stack.copy();
        assert items.length == sizeY && sizeY >= 1;
        assert items[0].length == sizeX;
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                final var slot = items[y][x];
                if (slot == null) {
                    items[y][x] = copy;
                    return;
                } else if (slot.item().equals(copy.item())) {
                    final var left = slot.spaceLeft();
                    if (left > 0) {
                        final var amount = copy.amount();
                        final var toAdd = Math.min(amount, left);
                        slot.amount(slot.amount() + toAdd);
                        copy.amount(amount - toAdd);
                    } else {
                        //skip
                    }
                } else {
                    //skip
                }
            }
        }

    }

    private void merge() {

    }
}
