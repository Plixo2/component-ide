package de.plixo.game.blocks;

import de.plixo.game.ItemInventory;
import de.plixo.game.item.ItemStack;
import de.plixo.general.collision.EnumFacing;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockMachine extends InventoryBlock {
    private boolean eject = false;
    private boolean input = false;

    private boolean ejectNorth = false;
    private boolean ejectSouth = false;
    private boolean ejectUp = false;
    private boolean ejectDown = false;
    private boolean ejectWest = false;
    private boolean ejectEast = false;

    private boolean inputNorth = false;
    private boolean inputSouth = false;
    private boolean inputUp = false;
    private boolean inputDown = false;
    private boolean inputWest = false;
    private boolean inputEast = false;

    @NotNull ItemStack[] producedSlots = new ItemStack[0];

    int ejectionOffset = 0;

    @Override
    public void onUpdate() {

        if (eject) {
            for (int i = 0; i < producedSlots.length; i++) {
                ItemStack producedItem = producedSlots[i];
                for (EnumFacing value : EnumFacing.values()) {
//                    if (shouldEject(value)) {
//                        val result = eject(producedItem);
//                        producedSlots[i] = merge(result);
//                        if (EjectionResult.hasMovedAny()) {
//                            break;
//                        }
//                    }
                }
            }
        }

        super.onUpdate();
    }

    private EjectionResult eject(@NotNull ItemInventory inventory, ItemStack stack) {
//        return ;
        return null;
    }

    private @Nullable ItemStack merge(@NotNull EjectionResult result,
                                      ItemStack producedItem) {
        if(result.wasSuccessful()) {
            return null;
        }
        if(!result.hasMovedAny()) {
            return producedItem;
        }
        return result.left();
    }

    private boolean shouldEject(@NotNull EnumFacing face) {
        return switch (face) {
            case DOWN -> ejectDown;
            case UP -> ejectUp;
            case NORTH -> ejectNorth;
            case SOUTH -> ejectSouth;
            case WEST -> ejectWest;
            case EAST -> ejectEast;
        };
    }

    private boolean canInput(@NotNull EnumFacing face) {
        return switch (face) {
            case DOWN -> inputDown;
            case UP -> inputUp;
            case NORTH -> inputNorth;
            case SOUTH -> inputSouth;
            case WEST -> inputWest;
            case EAST -> inputEast;
        };
    }


//    public void
}
