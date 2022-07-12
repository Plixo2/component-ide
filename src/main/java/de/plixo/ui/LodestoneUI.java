package de.plixo.ui;

import com.plixo.ui.elements.UIElement;

public class LodestoneUI {

    public LodestoneUI(com.plixo.ui.IRenderer renderer , com.plixo.ui.IKeyboard keyboard , com.plixo.ui.IMouse mouse) {
        UIElement.gui = renderer;
        UIElement.keyboard = keyboard;
        UIElement.mouse = mouse;
    }
}
