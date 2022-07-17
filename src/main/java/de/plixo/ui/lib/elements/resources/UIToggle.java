package de.plixo.ui.lib.elements.resources;

import de.plixo.animation.Ease;
import de.plixo.general.Color;
import de.plixo.general.Util;
import de.plixo.systems.RenderSystem;
import de.plixo.ui.lib.elements.UIElement;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.elements.visuals.UIEmpty;
import de.plixo.ui.lib.general.ColorLib;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public class UIToggle extends UIReference<Boolean> {

    @Setter
    @Accessors(fluent = true)
    private @NotNull Color primaryColor = new Color(ColorLib.getBackground(0.1f));

    public UIToggle() {
        setOutlineColor(ColorLib.getBackground(0.4f));
        setOutlineWidth(4f);
    }
    float anim = 0;
    @Override
    public void drawScreen(float mouseX, float mouseY) {

        final var state = reference.getValue();

        defaultBackground();
        defaultHover();
        defaultUpdate(mouseX, mouseY);

        GUI.drawRoundedRect(x+2, y + 2, x + height * 2 - 4, y + height - 2,30, primaryColor.getRgba());
        GUI.drawLinedRoundedRect(x+2, y + 2, x + height * 2 - 4, y + height - 2,30,getOutlineColor(), 2f);

        int target = state ? 1 : -1;
        anim = Util.clamp01(anim + target * RenderSystem.INSTANCE.delta_time() * 4f);
        final var color = new Color(0xFFFF2B39).mix(new Color(0xFF26DE38), anim);
        final var line = Ease.OutExpo.animate(anim);
        GUI.drawCircle(x + height/2 + ((height-2) * 1f * line), y + height / 2, height/2f - 5 , color.getRgba() );

    }

    @Override
    public void setDimensions(float x, float y, float width, float height) {

        assert reference.getValue() != null;

        if(reference.getValue()) {
            anim = 1;
        }
        UIElement element = new UIEmpty();
        element.setDimensions(0, 0, width, height);

        element.setAction(() -> {
            reference.setValue(!reference.getValue());
        });
        add(element);


        super.setDimensions(x, y, width, height);
    }
}
