package de.plixo.intermediate;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.UIChildEvent;
import de.plixo.general.Color;
import de.plixo.general.reference.InterfaceReference;
import de.plixo.intermediate.statement.ExpressionStatement;
import de.plixo.intermediate.statement.Statement;
import de.plixo.intermediate.statement.StatementList;
import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.elements.other.UIState;
import de.plixo.ui.lib.elements.resources.UIButton;
import de.plixo.ui.lib.elements.resources.UITextBox;
import org.jetbrains.annotations.NotNull;

public class UI {

    static UICanvas canvas;

    @SubscribeEvent
    static void ui(@NotNull UIChildEvent event) {
        canvas = event.canvas().add(new UICanvas());
        init();
    }

    static void init() {
        Function function = new Function();
        function.name = "Bye World";
        new UIState<>(canvas, function, (f, canvas) -> {
            var functionCanvas = canvas.create(UIAlign.class);
            functionCanvas.alignVertical();

            final UIAlign head = functionCanvas.create(UIAlign.class);
            head.alignHorizontal();

            var box = head.create(UITextBox.class);
            box.writeBackOnFocusLost();
            box.setRoundness(0);
            box.setSelectionOutlineColor(0);
            box.setOutlineColor(0);
            box.setReference(new InterfaceReference<>(f::name, f::name));
            box.setDimensions(0, 0, 80, 20);


            var button = head.create(UIButton.class);
            button.setDisplayName("+");
            button.setDimensions(0, 0, 20, 20);
            button.setAction(() -> {
                f.arguments.add(new Function.Argument());
            });

            f.arguments.forEach(ref -> {
                var argCanvas = head.create(UICanvas.class);
                var text = argCanvas.create(UITextBox.class);
                text.setReference(new InterfaceReference<>(ref::name, ref::name));
                text.setDimensions(0, 0, 60, 15);

                text = argCanvas.create(UITextBox.class);
                text.setOutlineColor(Color.ORANGE.rgba());
                text.setReference(new InterfaceReference<>(ref::type, ref::type));
                text.setDimensions(0, 15, 60, 15);

                argCanvas.pack();
            });

            if (f.statement != null) {
                new UIState<>(functionCanvas, f.statement, UI::statement);
            }
            var statement = head.create(UIButton.class);
            statement.setDimensions(0, 0, 10, 10);
            statement.setAction(() -> {
                if (f.statement == null) f.statement = new StatementList();
                ((StatementList) f.statement).list.add(new ExpressionStatement());
            });

            head.pack();
            System.out.println("created");
        });
    }

    static void statement(@NotNull Statement statement, @NotNull UICanvas canvas) {
        if (statement instanceof ExpressionStatement expr) {
            var textBox = canvas.create(UITextBox.class);
            textBox.writeBackOnFocusLost();
            textBox.setRoundness(0);
            textBox.setOutlineColor(0);
            textBox.setSelectionOutlineColor(0);
            textBox.setReference(new InterfaceReference<>(expr::str, expr::str));
            textBox.setDimensions(0, 0, 100, 14);
        } else if (statement instanceof StatementList list) {
            var align = canvas.create(UIAlign.class);
            list.list.forEach(ref -> {
                statement(ref, align);
            });
            align.pack();
        }
    }
}
