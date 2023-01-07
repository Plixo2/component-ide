package de.plixo.ui.lib.elements.other;

import de.plixo.ui.lib.general.Icon;
import de.plixo.ui.lib.elements.UIElement;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UIIcon extends UIElement {

    @Setter
    @Nullable Icon icon;
    public UIIcon() {
        setColor(-1);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        assert icon != null;
        icon.texture.drawStatic(x, y, x + width, y + height,0,0,1,1,getColor());
    }

    public void load(@NotNull String str) {
        this.icon = new Icon(str);
    }
}
