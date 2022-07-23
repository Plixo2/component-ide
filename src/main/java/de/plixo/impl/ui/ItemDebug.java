package de.plixo.impl.ui;

import de.plixo.game.item.ItemStack;
import de.plixo.general.Color;
import de.plixo.general.reference.InterfaceReference;
import de.plixo.general.reference.Reference;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.elements.other.UIPopup;
import de.plixo.ui.lib.elements.resources.UIFillBar;
import de.plixo.ui.lib.elements.resources.UISpinner;
import de.plixo.ui.lib.elements.resources.UITextBox;
import de.plixo.ui.lib.elements.visuals.UIEmpty;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.general.UIManager;

public class ItemDebug  {


    public static void displayMenu(ItemStack reference) {

        final var wind = UIManager.displayModalWindow("Debug",true);
        final var canvas = wind.getCanvas();
        canvas.setColor(ColorLib.getBackground(0.0f));


        UIAlign align = new UIAlign();

        UIEmpty empty = new UIEmpty();
        empty.alignTextLeft();
        empty.setDisplayName("Name");
        empty.setDimensions(0,0,100,10);
        align.add(empty);
        UITextBox textBox = new UITextBox();
        textBox.setReference(new InterfaceReference<>(reference.item()::localName,reference.item()::localName));
        textBox.setDimensions(0,0,100,15);
        align.add(textBox);

        empty = new UIEmpty();
        empty.alignTextLeft();
        empty.setDisplayName("Amount");
        empty.setDimensions(0,0,100,10);
        align.add(empty);
        UISpinner spinner = new UISpinner();
        spinner.setReference(new InterfaceReference<>(reference::amount,reference::amount));
        spinner.setDimensions(0,0,100,15);
        align.add(spinner);

        empty = new UIEmpty();
        empty.alignTextLeft();
        empty.setDisplayName("Durability");
        empty.setDimensions(0,0,100,10);
        align.add(empty);
        UIFillBar fillBar = new UIFillBar();
        fillBar.setReference(new InterfaceReference<>(reference::durability,reference::durability));
        fillBar.setDrawNumber(true);
        fillBar.fade(false);
        fillBar.primaryColor(new Color(ColorLib.getMainColor(0f)));
        fillBar.setDraggable(true);
        fillBar.setDimensions(0,0,100,9);
        align.add(fillBar);


        align.pack();
        align.height = canvas.height;
        canvas.add(align);



    }
}
