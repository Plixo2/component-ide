package de.plixo.ui;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.UIChildEvent;
import de.plixo.game.ItemInventory;
import de.plixo.ui.lib.elements.UIElement;
import de.plixo.ui.lib.elements.layout.UIContext;
import de.plixo.impl.ui.UIInventory;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.general.UIManager;
import org.jetbrains.annotations.NotNull;

public class UIInvTest {

    @SubscribeEvent
    static void display(@NotNull UIChildEvent event) {
        final var canvas = event.canvas();


        UIContext context = new UIContext();
        context.setKeyContext((typedChar, keyCode) -> {
            if (keyCode == UIElement.KEYBOARD.KEY_ENTER()) {
                final var win = UIManager.displayDraggableModalWindow("TEst", 10,true);
                final var canvas_ = win.getCanvas();
                canvas_.setColor(ColorLib.getBackground(0.6f));
                ItemInventory inventory = new ItemInventory(5, 5);
                UIInventory uiInv = new UIInventory();
                uiInv.setInv(inventory);
                uiInv.setDimensions(0, 10, 200, 200);
                uiInv.pack();
                canvas_.add(uiInv);
            }
        });
        canvas.add(context);


    }
}
