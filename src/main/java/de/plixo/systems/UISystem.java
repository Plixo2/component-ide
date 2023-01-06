package de.plixo.systems;

import de.plixo.animation.Ease;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.UIInitEvent;
import de.plixo.general.Color;
import de.plixo.general.reference.ObjectReference;
import de.plixo.general.reference.Reference;
import de.plixo.state.UIState;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.elements.other.UIKeyAction;
import de.plixo.ui.lib.elements.other.UITexture;
import de.plixo.ui.lib.elements.resources.*;
import de.plixo.ui.lib.elements.visuals.UIEmpty;
import de.plixo.ui.lib.general.ColorLib;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class UISystem {


    @SubscribeEvent
    void ui(@NotNull UIInitEvent event) {
        final UICanvas canvas = event.canvas();
        if (true) {
            return;
        }
        //debug
        {
            UIAlign align = new UIAlign();
            align.setColor(0xFF0A0A0A);
            align.setSpacing(4);
            align.setRoundness(0);
            align.setDimensions(-100, 0, 100, canvas.height);
            align.disableScissor();
            UIState.debugList = align;
            canvas.add(align);
            UICanvas top_bar = align.add(new UICanvas());
            top_bar.setDimensions(0, 0, 0, 15);
            val button = top_bar.add(new UIButton());
            button.setColor(ColorLib.getBackground(0.1f));
            button.setOutlineWidth(0);
            button.setDimensions(align.width, 0, 0, 0);
            button.disableTextShadow();
            AtomicBoolean toggle = new AtomicBoolean(false);
            final var width = align.width;
            button.setAction(() -> {
                if (!toggle.get()) {
                    AnimationSystem.animate(ref -> {
                        align.x = ref;
                    }, -width, 0f, 0.2f, Ease.InOutSine);
                } else {
                    AnimationSystem.animate(ref -> {
                        align.x = ref;
                    }, 0, -width, 0.2f, Ease.InOutSine);
                }
                toggle.set(!toggle.get());
            });
            top_bar.add(UIKeyAction.wrap(new ObjectReference<>(GLFW.GLFW_KEY_T), button));
            UIEmpty empty = top_bar.add(new UIEmpty());
            empty.setDimensions(0, 0, top_bar.width, top_bar.height);
            empty.alignTextLeft();
            empty.setDisplayName("Debug");
        }


        if (!UIState.firstRun) {
            lastReset.clear();
            rememberedSets.forEach((key, ref_) -> {
                final Reference<?> reference = ref_.getReference();
                if (reference != null) lastReset.put(key, reference.getValue());
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

    public static <T> Reference<T> reflect(@NotNull String name, @Nullable T defaults,
                                           Function<T, UIReference<T>> callback) {
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

}
