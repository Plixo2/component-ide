package de.plixo.game.item;

import de.plixo.game.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ItemStack {

    public static int STACK_SIZE = 64;

    @Getter
    @Accessors(fluent = true)
    int metaA = 0;

    @Getter
    @Accessors(fluent = true)
    int metaB = 0;

    @Getter
    @Accessors(fluent = true)
    int metaC = 0;
    @Getter
    @Setter
    @Accessors(fluent = true)
    private float durability = 0;


    @Getter
    @Setter
    @Accessors(fluent = true)
    int amount = 0;

    @Getter
    @Accessors(fluent = true)
    final @NotNull Item item;


    @Getter
    @Accessors(fluent = true)
    final String[] attributes = new String[0];


    public void interact(@NotNull Block block) {

    }


    public int spaceLeft() {
        return STACK_SIZE - amount;
    }

    public ItemStack copy() {
        final var itemStack = new ItemStack(item);
        itemStack.amount = amount;
        itemStack.durability = durability;
        itemStack.metaA = metaA;
        itemStack.metaB = metaB;
        itemStack.metaC = metaC;
        return itemStack;
    }

}
