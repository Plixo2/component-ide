package de.plixo.ui.general;


import de.plixo.ui.elements.UIElement;
import de.plixo.ui.interfaces.IKeyboard;
import de.plixo.ui.interfaces.IMouse;
import de.plixo.ui.interfaces.IRenderer;

public class LodestoneUI {

    public LodestoneUI(IRenderer renderer , IKeyboard keyboard , IMouse mouse) {
        UIElement.GUI = renderer;
        UIElement.KEYBOARD = keyboard;
        UIElement.MOUSE = mouse;
    }
}
