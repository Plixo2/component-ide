package de.plixo;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.UIChildEvent;
import de.plixo.general.Color;
import de.plixo.general.reference.ExposedReference;
import de.plixo.general.reference.InterfaceReference;
import de.plixo.general.reference.ObjectReference;
import de.plixo.intermediate.function.Argument;
import de.plixo.intermediate.function.Function;
import de.plixo.intermediate.function.types.ArrayType;
import de.plixo.intermediate.function.types.PrimitiveType;
import de.plixo.intermediate.function.types.Type;
import de.plixo.intermediate.statement.Statement;
import de.plixo.intermediate.ui.UIIntermediate;
import de.plixo.ui.lib.elements.layout.*;
import de.plixo.ui.lib.elements.other.UIIcon;
import de.plixo.ui.lib.elements.other.UITexture;
import de.plixo.ui.lib.elements.other.menu.MenuList;
import de.plixo.ui.lib.elements.other.menu.MenuObject;
import de.plixo.ui.lib.elements.other.menu.UIMenu;
import de.plixo.ui.lib.elements.resources.*;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.general.Icon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

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
        canvas.setColor(ColorLib.getBackground(0));

        var uiAlign = canvas.create(UIAlign.class);
        uiAlign.setHoverColor(0);
        uiAlign.setDimensions(5, 0, canvas.width, canvas.height);
        uiAlign.disableScissor();
        uiAlign.alignVertical();
        uiAlign.setSpacing(10);

        var uiButton = uiAlign.create(UIButton.class);
        uiButton.scaleDimensions(100, 28);
        uiButton.setDisplayName("Click Me");
        uiButton.setAction(() -> {
            var topLevelEntry = new MenuList<String>(new ArrayList<>(), "Object");
            var secodn = new MenuList<String>(new ArrayList<>(), "subList");
            topLevelEntry.add(new MenuObject<>("create new object"));
            topLevelEntry.add(new MenuObject<>("create new other"));
            secodn.add(new MenuObject<>("create new mee"));
            secodn.add(new MenuObject<>("create new aaaa"));
            topLevelEntry.add(secodn);

            UIMenu.displayMenu(100, 20, (o, w, h) -> {
                UIButton button = new UIButton();
                button.scaleDimensions(w, h);
                button.setDisplayName(o);
                return button;
            }, topLevelEntry, 60);
        });

        var color = uiAlign.create(UIColor.class);
        color.setReference(new ObjectReference<>(new Color(0xFF00FF00)));
        color.setDimensions(0, 30, 80, 60);

        var uiSpinner = uiAlign.create(UISpinner.class);
        uiSpinner.setReference(new ObjectReference<>(0));
        uiSpinner.scaleDimensions(80, 20);

        var cover = uiAlign.create(UICanvas.class);
        cover.setDimensions(0, 0, 0, 0);
        cover.enableScissor();

        var uiGrid = cover.create(UIGrid.class);

        uiGrid.setDimensions(0, 0, 100, 100);
        uiGrid.setShouldDrawLines(true);
        uiGrid.disableScissor();

        var uiDraggable = uiGrid.create(UIDraggable.class);
        uiDraggable.setDimensions(0, 0, 100, 100);
        uiDraggable.disableScissor();
        uiDraggable.setColor(ColorLib.getMainColor(0.5f));
        cover.scaleDimensions(uiGrid);

        var texture = uiAlign.create(UITexture.class);
        texture.load("content/icons/block.png");
        texture.setDimensions(0, 0, 100, 100);

        var uiFillBar = uiAlign.create(UIFillBar.class);
        uiFillBar.setReference(new ObjectReference<>(0.5f));
        uiFillBar.setDimensions(0, 0, 60, 10);
        uiFillBar.setDraggable(true);
        uiFillBar.setDrawNumber(true);

        var uiToggle = uiAlign.create(UIToggle.class);
        uiToggle.setReference(new ObjectReference<>(true));
        uiToggle.setDimensions(0, 0, 40, 20);


        var uiTextBox = uiAlign.create(UITextBox.class);
        uiTextBox.writeBackOnFocusLost();
        uiTextBox.setReference(new InterfaceReference<>(ref -> {
            try {
                uiToggle.getReference().setValue(Boolean.parseBoolean(ref));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, () -> String.valueOf(uiToggle.getReference().getValue())));
        uiTextBox.setDimensions(0, 0, 100, 20);

        var uiScrollBar = canvas.create(UIScrollBar.class);
        uiScrollBar.setReference(new InterfaceReference<>(uiAlign::setScrollPercent, uiAlign::getScrollPercent));
        uiScrollBar.setDimensions(0, 0, 5, canvas.height);

        var uiNumber = uiAlign.create(UINumber.class);
        uiNumber.setReference(new ObjectReference<>(0.5f));
        uiNumber.setDimensions(0, 0, 60, 10);

        canvas.clear();
        record Action(Icon icon, Runnable action, String name) {

        }

        var toolbar = new ArrayList<Action>();

        var collection = new UICollection<Action>();
        collection.addTo(canvas);
        collection.setDimensions(0, 0, canvas.width, 20);
        collection.alignHorizontal();
        collection.setList(toolbar);
        collection.setSpacing(10);
        collection.setSupplier(action -> {
            var canvas = new UICanvas();
            canvas.setDimensions(0,0,20,20);
            var icon = canvas.create(UIIcon.class) ;
            icon.setIcon(action.icon);
            icon.scaleDimensions(canvas);
            canvas.setHoverColor(0);
            canvas.setAction(action.action);
            return canvas;
        });

        toolbar.add(new Action(new Icon("edit"), () -> {
            System.out.println("Edit file");
        }, "Edit"));

        toolbar.add(new Action(new Icon("camera"), () -> {
            System.out.println("took screenshot");
        }, "Screenshot"));

        Function function = new Function("test", new ArrayList<>(), new ArrayType(new PrimitiveType("void")), new Statement());
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


        });//.addTo(canvas);


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
