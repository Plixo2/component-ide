package de.plixo.ui.elements.layout;

import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
public class UIContext extends UICanvas {

    @Nullable UIContext.DrawContext drawContext;
    @Nullable UIContext.MouseContext mouseClickContext;
    @Nullable UIContext.MouseContext mouseReleaseContext;
    @Nullable UIContext.KeyContext keyContext;


    @Override
    public void drawScreen(float mouseX, float mouseY) {
        super.drawScreen(mouseX, mouseY);
        if (drawContext != null) {
            drawContext.accept(mouseX, mouseY);
        }
    }


    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (mouseClickContext != null) mouseClickContext.accept(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        if (mouseReleaseContext != null) mouseReleaseContext.accept(mouseX, mouseY, state);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyContext != null) keyContext.accept(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    public interface KeyContext {
        void accept(char typedChar, int keyCode);
    }

    public interface DrawContext {
        void accept(float x, float y);
    }

    public interface MouseContext {
        void accept(float x, float y, int button);
    }

}
