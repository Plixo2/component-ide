package de.plixo.ui.lib.general;


import de.plixo.ui.lib.elements.UIElement;
import de.plixo.ui.lib.interfaces.IKeyboard;
import de.plixo.ui.lib.interfaces.IMouse;
import de.plixo.ui.lib.interfaces.IRenderer;

public class LodestoneUI {

    public LodestoneUI(IRenderer renderer , IKeyboard keyboard , IMouse mouse) {
        UIElement.GUI = renderer;
        UIElement.KEYBOARD = keyboard;
        UIElement.MOUSE = mouse;
    }
}
