package de.plixo.ui.impl.elements;

import de.plixo.game.ItemInventory;
import de.plixo.general.reference.InterfaceReference;
import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.elements.layout.UIScalar;
import de.plixo.ui.lib.general.ColorLib;
import lombok.Setter;

public class UIInventory extends UICanvas {

    public UIInventory() {
        setColor(0xFF0A0A0A);
    }

    @Setter
    ItemInventory inv;

    @Override
    public void setDimensions(float x, float y, float width, float height) {
        assert inv != null;

        UIScalar scalar = new UIScalar();

        UIAlign vertical = new UIAlign();
        vertical.setCanScroll(false);
        vertical.alignVertical();
        for (int y_ = 0; y_ < inv.sizeY(); y_++) {
            UIAlign horizontal = new UIAlign();
            horizontal.setCanScroll(false);
            horizontal.alignHorizontal();
            for (int x_ = 0; x_ < inv.sizeX(); x_++) {
                UIItem slot = new UIItem();
                int finalY_ = y_;
                int finalX_ = x_;
                slot.setReference(new InterfaceReference<>(s -> {
                    inv.items()[finalY_][finalX_] = s;
                }, () -> inv.items()[finalY_][finalX_]));
                slot.setDimensions(0, 0, 30, 30);
                slot.setColor(ColorLib.getBackground(0.1f));
                horizontal.add(slot);
            }
            horizontal.pack();
            vertical.add(horizontal);
        }
        vertical.pack();
        scalar.add(vertical);
        scalar.setScale(0.75f);
        scalar.pack();
        this.add(scalar);

        super.setDimensions(x, y, width, height);
    }
}
