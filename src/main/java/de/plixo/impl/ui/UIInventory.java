package de.plixo.impl.ui;

import de.plixo.game.ItemInventory;
import de.plixo.general.Color;
import de.plixo.general.reference.InterfaceReference;
import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.elements.layout.UIScalar;
import de.plixo.ui.lib.elements.visuals.UIEmpty;
import de.plixo.ui.lib.general.ColorLib;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

public class UIInventory extends UICanvas {

    @Getter
    private @Nullable UIAlign vertical;

    public UIInventory() {
        setColor(0xFF0A0A0A);
    }

    @Setter
    ItemInventory inv;

    @Setter
    @Getter
    @Accessors(fluent = true)
    boolean autoWrap = false;

    @Setter
    @Getter
    @Accessors(fluent = true)
    float itemSize = 30;

    @Setter
    @Getter
    @Accessors(fluent = true)
    float scale = 0.75f;

    @Override
    public void setDimensions(float x, float y, float width, float height) {
        assert inv != null;

        UIScalar scalar = new UIScalar();


        float invScale = 1 / scale;
        int spacing = 2;

        if (autoWrap) {

            vertical =new UIAlign();
            vertical.enableScissor();
            vertical.scaleDimensions(width * invScale,height * invScale);
            vertical.setCanScroll(true);
            vertical.setSpacing(spacing);
            vertical.alignVertical();

            vertical.add(new UIEmpty());

            boolean dirty = false;
            int item_x = 0;
            int item_y = 0;


            UIAlign horizontal = new UIAlign();
            horizontal.setCanScroll(false);
            horizontal.alignHorizontal();
            horizontal.setSpacing(spacing);



            for (int y_ = 0; y_ < inv.sizeY(); y_++) {
                for (int x_ = 0; x_ < inv.sizeX(); x_++) {
                    UIItem slot = new UIItem();

                    int finalY_ = y_;
                    int finalX_ = x_;
                    slot.setSelectionOutlineColor(ColorLib.getMainColor(0.3f));
                    slot.setOutlineWidth(1.5f);
                    slot.setOutlineColor(0);
                    slot.setReference(new InterfaceReference<>(s -> {
                        inv.items()[finalY_][finalX_] = s;
                    }, () -> inv.items()[finalY_][finalX_]));
                    slot.setColor(ColorLib.getBackground(0.1f));
                    slot.setDimensions(0, 0, itemSize, itemSize);

                    item_x += itemSize+spacing;
                    dirty = true;
                    horizontal.add(slot);
                    if (item_x+itemSize+spacing >= vertical.width - 1) {
                        item_x = 0;
                        item_y += itemSize+spacing;

                        horizontal.pack();
                        vertical.add(horizontal);
                        horizontal = new UIAlign();
                        horizontal.setSpacing(spacing);
                        horizontal.setCanScroll(false);
                        horizontal.alignHorizontal();
                        dirty = false;
                    }

                }
            }
            if(dirty) {
                horizontal.pack();
                vertical.add(horizontal);
            }

            scalar.add(vertical);
            scalar.setScale(scale);
            scalar.pack();
            this.add(scalar);
        } else {

            UIAlign vertical = new UIAlign();
            vertical.setCanScroll(false);
            vertical.setSpacing(spacing);
            vertical.alignVertical();
            for (int y_ = 0; y_ < inv.sizeY(); y_++) {
                UIAlign horizontal = new UIAlign();
                horizontal.setCanScroll(false);
                horizontal.alignHorizontal();
                horizontal.setSpacing(spacing);
                for (int x_ = 0; x_ < inv.sizeX(); x_++) {
                    UIItem slot = new UIItem();
                    int finalY_ = y_;
                    int finalX_ = x_;
                    slot.setReference(new InterfaceReference<>(s -> {
                        inv.items()[finalY_][finalX_] = s;
                    }, () -> inv.items()[finalY_][finalX_]));
                    slot.setDimensions(0, 0, itemSize, itemSize);
                    slot.setColor(ColorLib.getBackground(0.1f));
                    horizontal.add(slot);
                }
                horizontal.pack();
                vertical.add(horizontal);
            }
            vertical.pack();
            scalar.add(vertical);
            scalar.setScale(scale);
            scalar.pack();
            this.add(scalar);
        }

        super.setDimensions(x, y, width, height);
    }
}
