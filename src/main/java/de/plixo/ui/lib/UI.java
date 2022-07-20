package de.plixo.ui.lib;

import de.plixo.animation.Animation;
import de.plixo.animation.Ease;
import de.plixo.event.Dispatcher;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.InitEvent;
import de.plixo.event.impl.ShutDownEvent;
import de.plixo.event.impl.UIChildEvent;
import de.plixo.event.impl.UIInitEvent;
import de.plixo.general.Color;
import de.plixo.general.FileUtil;
import de.plixo.general.IO;
import de.plixo.general.Util;
import de.plixo.general.reference.InterfaceReference;
import de.plixo.general.reference.ObjectReference;
import de.plixo.general.reference.Reference;
import de.plixo.state.UIState;
import de.plixo.systems.RenderSystem;
import de.plixo.ui.impl.elements.UITexture;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.elements.resources.*;
import de.plixo.ui.lib.elements.visuals.UIEmpty;
import de.plixo.ui.lib.general.ColorLib;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

public class UI {


    @SubscribeEvent
    static void ui(@NotNull UIInitEvent event) {
        final UICanvas canvas = event.canvas();
        UICanvas left = new UICanvas();
        left.setDimensions(0, 0, 160, canvas.height);
        left.setRoundness(0);
        left.setColor(0xFF0A0A0A);
        canvas.add(left);

        UICanvas worldCanvas = new UICanvas();
        worldCanvas.setColor(0);
        worldCanvas.setRoundness(0);
        worldCanvas.setDimensions(160, 30, canvas.width - 160, canvas.height - 30);
        canvas.add(worldCanvas);
        RenderSystem.INSTANCE.worldcanvas(worldCanvas);
        final var tex = new UITexture() {
            @Override
            public void drawScreen(float mouseX, float mouseY) {
                IO.setCanvasMouse(new Vector2f(mouseX * RenderSystem.UI_SCALE, mouseY * RenderSystem.UI_SCALE));
                super.drawScreen(mouseX, mouseY);
            }
        };
        tex.scaleDimensions(worldCanvas);
        worldCanvas.add(tex);
        tex.texture(
                        RenderSystem.INSTANCE.worldTarget().get(GL_COLOR_ATTACHMENT0).as_texture()
                );
//        IO.setCanvasPosition(new Vector2f(worldCanvas.x,worldCanvas.y));

        UIAlign align = new UIAlign();
        align.setSpacing(4);
        align.setDimensions(0, 40, 160, canvas.height - 40);
        align.enableScissor();
        UIState.debugList = align;
        canvas.add(align);


        UIFillBar camDistance = new UIFillBar();
        camDistance.setDimensions(165, canvas.height - 8, canvas.width - 190, 6);
        camDistance.setReference(new InterfaceReference<>(RenderSystem.INSTANCE.camera()::xOffset,
                RenderSystem.INSTANCE.camera()::xOffset));
        camDistance.setMin(-5);
        camDistance.setMax(5);
        camDistance.setDraggable(true);
        canvas.add(camDistance);

        UIScrollBar scrollBar = new UIScrollBar();
        scrollBar.setDimensions(canvas.width - 8, 35, 6, canvas.height - 60);
        scrollBar.setReference(new InterfaceReference<>(RenderSystem.INSTANCE.camera()::yOffset,
                RenderSystem.INSTANCE.camera()::yOffset));
        scrollBar.min(-5);
        scrollBar.max(5);
        canvas.add(scrollBar);

        UICanvas reset = new UICanvas();
        reset.setColor(ColorLib.getBackground(0.7f));
        reset.setDimensions(canvas.width - 20, canvas.height - 20, 25, 25);
        UITexture texture = new UITexture();
        texture.load("content/icons/camera.png");
        texture.setDimensions(5, 5, 10, 10);
        reset.add(texture);
        canvas.add(reset);
        reset.setAction(() -> {
            Animation.animate(s -> {
                RenderSystem.INSTANCE.camera().xOffset(s);
            }, RenderSystem.INSTANCE.camera().xOffset(), 0, 0.5f, Ease.InOutQuint);
            Animation.animate(s -> {
                RenderSystem.INSTANCE.camera().yOffset(s);
            }, RenderSystem.INSTANCE.camera().yOffset(), 0, 0.5f, Ease.InOutQuint);

            RenderSystem.INSTANCE.camera().yaw(Util.clampAngle(RenderSystem.INSTANCE.camera().yaw()));
            Animation.animate(s -> {
                RenderSystem.INSTANCE.camera().yaw(s);
            }, RenderSystem.INSTANCE.camera().yaw(), 45, 1f, Ease.InOutQuint);

            Animation.animate(s -> {
                RenderSystem.INSTANCE.camera().pitch(s);
            }, RenderSystem.INSTANCE.camera().pitch(), -45, 0.5f, Ease.InOutQuint);
        });


        UINumber x_center = new UINumber();
        x_center.setReference(new InterfaceReference<>(s -> {
            final Vector3f origin = RenderSystem.INSTANCE.camera().origin();
            origin.x = s;
        }, () -> RenderSystem.INSTANCE.camera().origin().x));
        x_center.setDimensions(10, 10, 40, 20);
        left.add(x_center);

        UINumber y_center = new UINumber();
        y_center.setReference(new InterfaceReference<>(s -> {
            final Vector3f origin = RenderSystem.INSTANCE.camera().origin();
            origin.y = s;
        }, () -> RenderSystem.INSTANCE.camera().origin().y));
        y_center.setDimensions(60, 10, 40, 20);
        left.add(y_center);

        UINumber z_center = new UINumber();
        z_center.setReference(new InterfaceReference<>(s -> {
            final Vector3f origin = RenderSystem.INSTANCE.camera().origin();
            origin.z = s;
        }, () -> RenderSystem.INSTANCE.camera().origin().z));
        z_center.setDimensions(110, 10, 40, 20);
        left.add(z_center);

        Dispatcher.emit(new UIChildEvent(canvas));

        if (!UIState.firstRun) {
            lastReset.clear();
            rememberedSets.forEach((key, ref_) -> {
                final Reference<?> reference = ref_.getReference();
                if (reference != null)
                    lastReset.put(key, reference.getValue());
            });
            rememberedSets.clear();
        }
        UIState.firstRun = false;

    }

    static Map<Integer, UIReference<?>> rememberedSets = new HashMap<>();
    static Map<Integer, Object> lastReset = new HashMap<>();

    public static boolean reflectBool(@NotNull String name, boolean defaults) {
        return reflect(name, defaults, (d) -> {
            UIToggle toggle = new UIToggle();
            toggle.setReference(new ObjectReference<>(d));
            return toggle;
        }).getValue();
    }

    public static float reflectFloat(@NotNull String name, float defaults, float min, float max) {
        return reflect(name, defaults, (d) -> {
            UIFillBar bar = new UIFillBar();
            bar.setReference(new ObjectReference<>(d));
            bar.setMax(max);
            bar.setMin(min);
            bar.setDraggable(true);
            bar.height = 10;
            return bar;
        }).getValue();
    }

    public static void reflectAction(@NotNull String name, Runnable callback) {
        val id = name.hashCode();
        val contains = rememberedSets.containsKey(id);
        if (!contains) {
            UICanvas canvas = new UICanvas();
            canvas.setColor(ColorLib.getBackground(0));
            canvas.setDimensions(5, 0, UIState.debugList.width - 10, 0);
            UIEmpty label = new UIEmpty();
            label.setDisplayName(name + ": ");
            label.setDimensions(2, 2, canvas.width - 4, 14);
            label.alignTextLeft();
            canvas.add(label);

            val o = lastReset.get(id);
            val element = new UIButton();
            element.setDisplayName("Run");
            element.setDimensions(2, 16, canvas.width - 4, 20);
            element.setAction(callback);
            canvas.add(element);
            canvas.pack();
            canvas.width += 2;
            canvas.height += 2;
            UIState.debugList.add(canvas);
            rememberedSets.put(id, new UIReference<>() {
            });
        }
    }


    public static float reflectFloat(@NotNull String name, float defaults) {
        return reflect(name, defaults, (d) -> {
            UINumber box = new UINumber();
            box.setReference(new ObjectReference<>(d));
            box.height = 15;
            return box;
        }).getValue();
    }

    public static Color reflectColor(@NotNull String name, Color defaults) {
        return reflect(name, defaults, (d) -> {
            UIColor color = new UIColor();
            color.setReference(new ObjectReference<>(d));
            color.height = 50;
            return color;
        }).getValue();
    }

    public static <T> Reference<T> reflect(@NotNull String name, @Nullable T defaults, Function<T,
            UIReference<T>> callback) {
        val id = name.hashCode();
        val contains = rememberedSets.containsKey(id);
        if (!contains) {
            UICanvas canvas = new UICanvas();
            canvas.setColor(ColorLib.getBackground(0));
            canvas.setDimensions(5, 0, UIState.debugList.width - 10, 0);
            UIEmpty label = new UIEmpty();
            label.setDisplayName(name + ": ");
            label.setDimensions(2, 2, canvas.width - 4, 14);
            label.alignTextLeft();
            canvas.add(label);

            val o = lastReset.get(id);
            val element = callback.apply(o == null ? defaults : (T) o);
            element.setDimensions(2, 16, canvas.width - 4, element.height == 0 ? 20 : element.height);
            canvas.add(element);
            canvas.pack();
            canvas.width += 2;
            canvas.height += 2;
            UIState.debugList.add(canvas);
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
            val str = FileUtil.loadAsString("content/config.txt");
            val split = str.split("\n");
            for (String s : split) {
                val stt = s.split("\\?");
                val id = Integer.parseInt(stt[0]);
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
