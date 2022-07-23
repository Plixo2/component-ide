package de.plixo.ui.lib.elements.other;

import de.plixo.general.reference.Reference;
import de.plixo.ui.lib.elements.UIElement;
import org.jetbrains.annotations.NotNull;

public class UIKeyAction extends UIElement {

    public static UIKeyAction wrap(Reference<Integer> key, @NotNull UIElement element) {
        return new UIKeyAction() {
            @Override
            public void keyTyped(char typedChar, int keyCode) {
                if (keyCode == key.getValue()) {
                    final var btn = element.getAction();
                    if (btn != null) {
                        btn.run();
                    }
                }
                super.keyTyped(typedChar, keyCode);
            }
        };
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        //nothing
    }
}
