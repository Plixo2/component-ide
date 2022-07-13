package de.plixo.ui.elements.other;

import de.plixo.ui.elements.UIElement;
import de.plixo.ui.elements.layout.UICanvas;
import de.plixo.ui.elements.layout.UIInvisible;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;


public class UIDropdown extends UICanvas {

    public UIDropdown() {
        super();
    }

    @Setter
    @Nullable UIElement content = null;

    @Override
    public void setDimensions(float x, float y, float width, float height) {
        assert content != null : "set content first";

        UIInvisible toggle = new UIInvisible();
        toggle.disable();
        toggle.setDimensions(0, height, content.width, content.height);
        toggle.add(content);
        toggle.enableScissor();
        add(toggle);

        setAction(toggle::toggle);


        super.setDimensions(x, y, width, height);
    }


}
