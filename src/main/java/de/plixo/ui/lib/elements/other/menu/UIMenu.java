package de.plixo.ui.lib.elements.other.menu;

import de.plixo.ui.lib.elements.UIElement;
import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.elements.layout.UISpacer;
import de.plixo.ui.lib.elements.other.UIPopup;
import de.plixo.ui.lib.elements.visuals.UIEmpty;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.general.UIManager;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;


public class UIMenu extends UICanvas {

    private UIMenu() {

    }

    public static <O> void displayMenu(float width, float height,
                                       @NotNull TriFunction<O, Float, Float, UIElement> objectFunction,
                                       @NotNull MenuList<O> topLevelEntry, float maxHeight) {
        final UIManager.Window win = UIManager.displayWindow("win");
        UIPopup popup = new UIPopup();
        UIAlign align = new UIAlign();
        align.setDimensions(0, 0, 1000, maxHeight);
        popup.setColor(ColorLib.getBackground(0.8f));

        popup.setDimensions(UIManager.INSTANCE.mouseX, UIManager.INSTANCE.mouseY, 200, 200);
        win.getCanvas().add(popup);

        final UIElement uiElement = makeObject(topLevelEntry, width, height, false, objectFunction);

        UIEmpty head = new UIEmpty();
        head.setDimensions(0, 0, width, height);
        head.setDisplayName(topLevelEntry.name());
        head.setColor(ColorLib.getBackground(0.4f));
        head.alignTextLeft();
        align.add(head);

        align.add(uiElement);
        align.pack();
        align.enableScissor();
        popup.add(align);
        popup.pack();
        popup.setOutlineColor(ColorLib.getMainColor(1));
        popup.setOutlineWidth(3f);

    }

    private static <O> UIElement makeObject(@NotNull MenuEntry<O> entry, float width, float height,
                                            boolean displayHead,
                                            @NotNull TriFunction<O, Float, Float, UIElement> objectFunction) {
        if (entry instanceof MenuList<O> list) {
            UIAlign align = new UIAlign();
            UIEmpty head = new UIEmpty();
            head.setDisplayName(list.name());
            head.alignTextMiddle();
            head.setDimensions(0, 0, width, height * 0.6f);
            if (displayHead) {
                align.add(head);
                UISpacer spacer = new UISpacer();
                spacer.setDimensions(5, 0, width - 10, 1);
                align.add(spacer);
            }
            align.setDimensions(0, 0, width, 10000);
            list.list().forEach(obj -> {
                final UIElement element = makeObject(obj, width - 10, height, true, objectFunction);
                element.x = 10;
                align.add(element);
            });
            align.pack();
            align.setColor(ColorLib.getBackground(0.8f));
            align.setHoverColor(0);
            align.setOutlineColor(0x33000000);
            align.setOutlineWidth(3);
            return align;
        } else if (entry instanceof MenuObject<O> object) {
            return objectFunction.apply(object.object(), width, height);
        } else
            throw new NullPointerException();
    }

    private void displayList(String... data) {
        //TODO
    }
}
