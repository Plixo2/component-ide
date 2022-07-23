package de.plixo.impl.ui;

import de.plixo.game.item.Item;
import de.plixo.game.item.ItemStack;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.elements.layout.UIContext;
import de.plixo.ui.lib.elements.layout.UIInvisible;
import de.plixo.ui.lib.elements.resources.UIButton;
import de.plixo.ui.lib.elements.visuals.UIEmpty;
import de.plixo.ui.lib.general.ColorLib;

import java.awt.*;

public class UIItem extends UIReference<ItemStack> {
    public UIItem() {
        setRoundness(0);
    }

    @Override
    public void setDimensions(float x, float y, float width, float height) {
        assert reference != null;

        UIEmpty button = new UIEmpty();
        button.setRoundness(0);
        button.scaleDimensions(width, height);
        button.setColor(0);
        this.add(button);


        button.setAction(() -> {
            if (reference.getValue() == null)
                reference.setValue(new ItemStack(new Item("Hello World")));
            ItemDebug.displayMenu(reference.getValue());
        });

        UIInvisible texWrapper = new UIInvisible();
        texWrapper.disable();
        UITexture texture = new UITexture();
        texture.load("content/icons/select.png");
        texture.scaleDimensions(width, height);
        texWrapper.add(texture);


        UIContext context = new UIContext();
        context.setDrawContext((mx, my) -> {
            final var value = reference.getValue();

//            texWrapper.setEnabled(context.isHovered(mx,my));

            if (value != null) {

                final var str = "x"+ value.amount();
                GUI.drawString(str, width - 2 - GUI.getStringWidth(str), 6, -1);
                GUI.drawRoundedRect(2, height - 4, width - 2, height - 2, 13, ColorLib.getBackground(1f));
                GUI.drawRoundedRect(2, height - 4, 2 + (width - 4) * value.durability(), height - 2, 13,
                        Color.HSBtoRGB((value.durability() - 0.1f) / 3,
                                1f,
                                1f));
            }
        });
        context.scaleDimensions(width,height);
        this.add(context);
        add(texWrapper);

        setOnTick(() -> {
            final var stack = reference.getValue();
            if (stack == null) {
//                button.setDisplayName("");
                button.setHoverName("");
            } else {
//                button.setDisplayName(String.valueOf(stack.amount()));
                button.setHoverName(stack.item().localName());
            }
        });

        super.setDimensions(x, y, width, height);
    }
}
