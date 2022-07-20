package de.plixo.game.blocks;

import de.plixo.game.Block;
import de.plixo.game.ItemInventory;
import de.plixo.ui.impl.elements.UIInventory;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.general.UIManager;
import lombok.val;
import org.jetbrains.annotations.Nullable;

public class InventoryBlock extends Block {
    private final ItemFilter inputFilter = new ItemFilter();
    private final ItemFilter ejectionFilter = new ItemFilter();

    protected @Nullable ItemInventory inventory = null;

    @Override
    public void interact() {
        assert inventory != null;
//        val inventory_debug = UIManager.displayDraggableModalWindow("Inventory Debug", 10, true);
        val canvas = new UICanvas();
        canvas.setColor(ColorLib.getBackground(0.6f));
        UIInventory uiInv = new UIInventory();
        uiInv.setInv(inventory);
        uiInv.setDimensions(0, 0, 200, 200);
        uiInv.pack();
        canvas.add(uiInv);
        canvas.pack();
        canvas.setDimensions(UIManager.INSTANCE.width/2 - canvas.width/2 ,
                UIManager.INSTANCE.height/2 - canvas.height/2 ,canvas.width,canvas.height );

        UIManager.addPopUp(canvas);
    }
}
