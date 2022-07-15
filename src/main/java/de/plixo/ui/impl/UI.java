package de.plixo.ui.impl;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.InitEvent;
import de.plixo.event.impl.ShutDownEvent;
import de.plixo.event.impl.UIInitEvent;
import de.plixo.general.Color;
import de.plixo.general.FileUtil;
import de.plixo.general.reference.InterfaceReference;
import de.plixo.general.reference.ObjectReference;
import de.plixo.general.reference.Reference;
import de.plixo.state.Window;
import de.plixo.ui.elements.UIReference;
import de.plixo.ui.elements.layout.UIAlign;
import de.plixo.ui.elements.layout.UICanvas;
import de.plixo.ui.elements.resources.UIColor;
import de.plixo.ui.elements.resources.UIFillBar;
import de.plixo.ui.elements.resources.UINumber;
import de.plixo.ui.elements.resources.UIToggle;
import de.plixo.ui.elements.visuals.UIEmpty;
import de.plixo.ui.general.ColorLib;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class UI {


    private static UIAlign align;

    private static boolean first = true;

    @SubscribeEvent
    static void ui(@NotNull UIInitEvent event) {
        final UICanvas canvas = event.canvas();
        UICanvas left = new UICanvas();
        left.setDimensions(0, 0, 160, canvas.height);
        left.setRoundness(0);
        left.setColor(0xFF0A0A0A);
        canvas.add(left);


        UIAlign align = new UIAlign();
        align.setSpacing(4);
        align.setDimensions(0, 40, 160, canvas.height - 40);
        UI.align = align;
        canvas.add(align);


        UIFillBar camDistance = new UIFillBar();
        camDistance.setDimensions(160, canvas.height - 10, canvas.width - 160, 10);
        camDistance.setReference(new InterfaceReference<>(Window.INSTANCE.camera()::distance,
                Window.INSTANCE.camera()::distance));
        camDistance.setMin(2);
        camDistance.setMax(30);
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



        if (!first) {
            lastReset.clear();
            rememberedSets.forEach((key, ref_) -> {
                lastReset.put(key, ref_.getReference().getValue());
            });
            rememberedSets.clear();
        }
        first = false;


    }

    static Map<Integer, UIReference<?>> rememberedSets = new HashMap<>();
    static Map<Integer, Object> lastReset = new HashMap<>();

    public static boolean reflectBool(int id, @NotNull String name, boolean defaults) {
        return reflect(id, name, defaults, (d) -> {
            UIToggle toggle = new UIToggle();
            toggle.setReference(new ObjectReference<>(d));
            return toggle;
        }).getValue();
    }

    public static float reflectFloat(int id, @NotNull String name, float defaults, float min, float max) {
        return reflect(id, name, defaults, (d) -> {
            UIFillBar bar = new UIFillBar();
            bar.setReference(new ObjectReference<>(d));
            bar.setMax(max);
            bar.setMin(min);
            bar.setDraggable(true);
            bar.height = 10;
            return bar;
        }).getValue();
    }


    public static float reflectFloat(int id, @NotNull String name, float defaults) {
        return reflect(id, name, defaults, (d) -> {
            UINumber box = new UINumber();
            box.setReference(new ObjectReference<>(d));
            box.height = 15;
            return box;
        }).getValue();
    }

    public static Color reflectColor(int id, @NotNull String name, Color defaults) {
        return reflect(id, name, defaults, (d) -> {
            UIColor color = new UIColor();
            color.setReference(new ObjectReference<>(d));
            color.height = 50;
            return color;
        }).getValue();
    }

    public static <T> Reference<T> reflect(int id, @NotNull String name, @Nullable T defaults,
                                           Function<T, UIReference<T>> callback) {
        final var contains = rememberedSets.containsKey(id);
        if (!contains) {

            UICanvas canvas = new UICanvas();
            canvas.setColor(ColorLib.getBackground(0));
            canvas.setDimensions(5, 0, align.width - 10, 0);
            UIEmpty label = new UIEmpty();
            label.setDisplayName(name + ": ");
            label.setDimensions(2, 2, canvas.width - 4, 14);
            label.alignTextLeft();
            canvas.add(label);

            final var o = lastReset.get(id);
            final var element = callback.apply(o == null ? defaults : (T) o);
            element.setDimensions(2, 16, canvas.width - 4, element.height == 0 ? 20 : element.height);
            canvas.add(element);
            canvas.pack();
            canvas.width += 2;
            canvas.height += 2;
            align.add(canvas);
            rememberedSets.put(id, element);
            return element.getReference();
        }

        return (Reference<T>) rememberedSets.get(id).getReference();
    }


    @SubscribeEvent
    static void shutDown(@NotNull ShutDownEvent event) {
        final StringBuilder builder = new StringBuilder();
        rememberedSets.forEach((k, v) -> {
            builder.append(k).append("?").append(v.getReference().getValue().getClass().getName()).append(":")
                    .append(v.getReference().getValue());
            builder.append("\n");
        });
        FileUtil.save(new File("content/config.txt"), builder.toString());
    }

    @SubscribeEvent
    static void load(@NotNull InitEvent event) {
        try {
            final var str = FileUtil.loadAsString("content/config.txt");
            final var split = str.split("\n");
            for (String s : split) {
                final var stt = s.split("\\?");
                final var id = Integer.parseInt(stt[0]);
                var clazz = stt[1].split(":")[0];
                var value = stt[1].split(":")[1].trim();
                Object obj;
                if (clazz.equalsIgnoreCase("de.plixo.general.Color")) {
                    obj = new Color(Integer.parseInt(value));
                } else if (clazz.equalsIgnoreCase("java.lang.Float")) {
                    obj = Float.parseFloat(value);
                } else if (clazz.equalsIgnoreCase("java.lang.Integer")) {
                    obj = Integer.parseInt(value);
                } else if (clazz.equalsIgnoreCase("java.lang.Boolean")) {
                    obj = Boolean.parseBoolean(value);
                } else {
                    throw new RuntimeException("Unknown class parser " + clazz);
                }
                lastReset.put(id, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
