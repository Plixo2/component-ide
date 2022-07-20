package de.plixo.game.blocks;

import de.plixo.game.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/*
 * left is null when everything was successful moves
 * hasMovedAny is true when anything has moved -> left could be null
 */

public record EjectionResult(@Nullable ItemStack left, boolean hasMovedAny) {

    public boolean wasSuccessful() {
        return left == null;
    }


}
