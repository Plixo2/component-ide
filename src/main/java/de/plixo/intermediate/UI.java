package de.plixo.intermediate;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.UIChildEvent;
import de.plixo.general.reference.ExposedReference;
import de.plixo.general.reference.ObjectReference;
import de.plixo.intermediate.function.Argument;
import de.plixo.intermediate.function.Function;
import de.plixo.intermediate.function.types.ArrayType;
import de.plixo.intermediate.function.types.PrimitiveType;
import de.plixo.intermediate.function.types.Type;
import de.plixo.intermediate.statement.Statement;
import de.plixo.intermediate.ui.UIIntermediate;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.general.ColorLib;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class UI {

    static UICanvas canvas;

    @SubscribeEvent
    static void ui(@NotNull UIChildEvent event) {
        UICanvas parent = event.canvas();
        canvas = parent.add(new UICanvas());
        canvas.scaleDimensions(parent);
        canvas.setRoundness(0);
        canvas.setHoverColor(0);
        init();
    }

    static void init() {

        Function function = new Function("test", new ArrayList<>(), new ArrayType(new PrimitiveType("void")), new Statement());

        canvas.setColor(ColorLib.getBackground(0));
        ObjectReference<String> reference = new ObjectReference<>("World");
        new UIIntermediate(uii -> {
            var GUI = uii.gui;


            function.name = uii.textbox(0, 0, 100, 20, "Function Name", function.name);


            var counter = new ExposedReference<>(0);
            float argWidth = uii.forEach(function.arguments, (argument, progress) -> {

                argument.name = uii.textbox(110 + progress, 0, 80, 20, "Argument " + counter.value++, argument.name);
                float width = drawTypeCanvas(uii, argument.type, 190 + progress, 0, 20);

                return 80f + width;
            });

            uii.icon("right", 110 + argWidth + 10, 0, 20);

            drawTypeCanvas(uii, function.returnType, 110 + argWidth + 40, 0, 20);

            if (uii.key(GLFW_KEY_P) && uii.control) {
                function.arguments.add(new Argument("", new PrimitiveType("void")));
            }


            float lineHeight = 15;
            float progress_ = uii.forEach(function.statement.foundTokens, (token, progress) -> {
                var width = GUI.getStringWidth(token.value) + 5;
                GUI.drawString(token.value, progress, 20 + lineHeight / 2f, token.color);
                return width;
            });

            function.statement.currentText = uii.textbox(progress_ + 5, 20, 100, lineHeight, "Token Input", function.statement.currentText);
            if (uii.key(GLFW_KEY_SPACE)) {
                function.statement.endToken();
            }


        }).addTo(canvas);

    }


    private static float drawTypeCanvas(UIIntermediate.UII uii, Type type, float x, float y, float height) {

        float width = getTypeWidth(uii, type);
        uii.gui.drawRoundedRect(x, y, x + width + 10, y + height, 3, ColorLib.getBackground(1f));
        drawType(uii, type, x + 5, y, height);
        return width;
    }

    private static float drawType(UIIntermediate.UII uii, Type type, float x, float y, float height) {
        if (type instanceof PrimitiveType primitive) {
            float stringWidth = uii.gui.getStringWidth(primitive.name);
            uii.gui.drawString(primitive.name, x, y + height / 2, 0xFFFF2424);
            return stringWidth;
        } else if (type instanceof ArrayType arrayType) {
            float width = drawType(uii, arrayType.elementType, x, y, height);
            uii.gui.drawString("[]", x + width + 3, y + height / 2f, 0xFFF26B1D);
            return width + 12;
        }
        throw new NullPointerException("not impl for " + type.getClass());
    }

    private static float getTypeWidth(UIIntermediate.UII uii, Type type) {
        if (type instanceof PrimitiveType primitive) {
            float stringWidth = uii.gui.getStringWidth(primitive.name);
            return stringWidth;
        } else if (type instanceof ArrayType arrayType) {
            return getTypeWidth(uii, arrayType.elementType) + 12;
        }
        throw new NullPointerException("not impl for " + type.getClass());
    }
}
//primitives red #FF2424
//arrays orange #F26B1D
//classes purple #8F2CF5
//enums Green #25BA73
//Generic Blue #2C6CBF
