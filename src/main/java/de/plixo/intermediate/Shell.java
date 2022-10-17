package de.plixo.intermediate;

import de.plixo.event.Dispatcher;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseClickEvent;
import de.plixo.general.Color;
import de.plixo.systems.UISystem;
import de.plixo.ui.lib.elements.UIElement;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.interfaces.IRenderer;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static de.plixo.systems.RenderSystem.UI_SCALE;

public class Shell {
    static {
        Shell.init();
        Dispatcher.registerStatic(Shell.class);
    }

    record Button(@NotNull Runnable action, float x, float y, float width, float height) {
    }

    static List<Button> buttons = new ArrayList<>();

    static Stack<Style> styles = new Stack<>();

    static IRenderer GUI;

    static void init() {
        GUI = UIElement.GUI;
    }

    static void draw() {
        buttons.clear();
        styles.clear();

        pushStyle(new Style(Color.WHITE, ColorLib.backgroundColor, ColorLib.mainColor,
                new Outline(Color.BLACK, 3), 10));
        button("Hello", () -> {
            System.out.println("Hello");
        });



    }

    public static void button(@NotNull String str, @NotNull Runnable action) {
        final Style style = styles.peek();
        final float width = style.outline.width /2;
        GUI.drawRoundedRect(0, 0, 100, 30, style.roundness, style.primary.rgba());
        if (width != 0) {
            GUI.drawLinedRoundedRect(0,0, 100, 30, style.roundness,
                    style.outline.color.rgba(), width);
        }
        GUI.drawCenteredString(str, 50, 15, style.textColor.rgba());
        buttons.add(new Button(action, 0, 0, 100, 30));
    }

    public static void pushStyle(@NotNull Style style) {
        styles.push(style);
    }

    @SubscribeEvent
    static void mouseClicked(@NotNull MouseClickEvent event) {
        val x = event.mouseX() / UI_SCALE;
        val y = event.mouseY() / UI_SCALE;

        if (event.button() == 0) {
            for (int i = buttons.size() - 1; i >= 0; i--) {
                val button = buttons.get(i);
                if (hovered(button.x, button.y, button.width, button.height, x, y)) {
                    button.action.run();
                    break;
                }
            }
        }
    }

    private static boolean hovered(float x, float y, float width, float height, float mx, float my) {
        return mx >= x && my >= y && mx < x + width && my < y + height;
    }

    record Style(@NotNull Color textColor, @NotNull Color background, @NotNull Color primary,
                 @NotNull Outline outline, float roundness

    ) {

    }

    record Outline(@NotNull Color color, float width) {

    }
}
