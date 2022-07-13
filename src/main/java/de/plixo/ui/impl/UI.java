package de.plixo.ui.impl;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.UIInitEvent;
import de.plixo.general.reference.InterfaceReference;
import de.plixo.state.Window;
import de.plixo.ui.elements.layout.UIAlign;
import de.plixo.ui.elements.layout.UICanvas;
import de.plixo.ui.elements.resources.UIFillBar;
import de.plixo.ui.elements.resources.UINumber;
import de.plixo.ui.elements.visuals.UILabel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class UI {

    public static float left_ = 0;
    public static float right = 0;
    public static float top = 0;
    public static float bottom = 0;

    @SubscribeEvent
    static void ui(@NotNull UIInitEvent event) {
        final UICanvas canvas = event.canvas();
        UICanvas left = new UICanvas();
        left.setDimensions(0, 0, 160, canvas.height);
        left.setRoundness(0);
        left.setColor(0xFF0A0A0A);
        canvas.add(left);


        UIAlign align = new UIAlign();
        align.setDimensions(0, 60, 160, canvas.height - 60);

        {
            UIFillBar fillBar = new UIFillBar();
            fillBar.setReference(new InterfaceReference<>(s -> left_ = s, () -> left_));
            fillBar.setDraggable(true);
            fillBar.setDrawNumber(true);
            fillBar.setDimensions(10,0,100,10);
            fillBar.setMax(0.05f);
            align.add(fillBar);
        }
        {
            UIFillBar fillBar = new UIFillBar();
            fillBar.setReference(new InterfaceReference<>(s -> top = s, () -> top));
            fillBar.setDraggable(true);
            fillBar.setDrawNumber(true);
            fillBar.setDimensions(10,0,100,10);
            fillBar.setMax(0.05f);
            align.add(fillBar);
        }
        {
            UIFillBar fillBar = new UIFillBar();
            fillBar.setReference(new InterfaceReference<>(s -> right = s, () -> right));
            fillBar.setDraggable(true);
            fillBar.setDrawNumber(true);
            fillBar.setDimensions(10,0,100,10);
            fillBar.setMax(0.05f);
            align.add(fillBar);
        }
        {
            UIFillBar fillBar = new UIFillBar();
            fillBar.setReference(new InterfaceReference<>(s -> bottom = s, () -> bottom));
            fillBar.setDraggable(true);
            fillBar.setDrawNumber(true);
            fillBar.setDimensions(10,0,100,10);
            fillBar.setMax(0.05f);
            align.add(fillBar);
        }

        canvas.add(align);



        UIFillBar camDistance = new UIFillBar();
        camDistance.setDimensions(160, canvas.height - 10, canvas.width - 160, 10);
        camDistance.setReference(new InterfaceReference<>(Window.INSTANCE.camera()::distance,
                Window.INSTANCE.camera()::distance));
        camDistance.setMin(2);
        camDistance.setMax(10);
        camDistance.setDraggable(true);
        canvas.add(camDistance);

        UINumber x_center = new UINumber();
        x_center.setReference(new InterfaceReference<>(s -> {
            final Vector3f origin = Window.INSTANCE.camera().origin();
            origin.x = s;
        }, () -> Window.INSTANCE.camera().origin().x));
        x_center.setDimensions(10, 10, 40, 20);
        left.add(x_center);

        UINumber y_center = new UINumber();
        y_center.setReference(new InterfaceReference<>(s -> {
            final Vector3f origin = Window.INSTANCE.camera().origin();
            origin.y = s;
        }, () -> Window.INSTANCE.camera().origin().y));
        y_center.setDimensions(60, 10, 40, 20);
        left.add(y_center);

        UINumber z_center = new UINumber();
        z_center.setReference(new InterfaceReference<>(s -> {
            final Vector3f origin = Window.INSTANCE.camera().origin();
            origin.z = s;
        }, () -> Window.INSTANCE.camera().origin().z));
        z_center.setDimensions(110, 10, 40, 20);
        left.add(z_center);

    }
}
