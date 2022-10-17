package de.plixo.ui.lib.elements.other;

import de.plixo.ui.lib.elements.State;
import de.plixo.ui.lib.elements.layout.UICanvas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class UIState<T extends Cloneable & State> extends UICanvas {
    T copy;
    T original;
    BiConsumer<T,UICanvas> builder;

    public UIState(@Nullable UICanvas canvas,@NotNull T original, @NotNull BiConsumer<T,UICanvas> builder) {
        copy = (T) original.copy();
        this.original = original;
        this.builder = builder;
        clear();
        builder.accept(original,this);
        if (canvas != null) {
            canvas.add(this);
        }
    }

    public UIState(T original, BiConsumer<T,UICanvas> builder) {
        this(null,original, builder);
    }

    int ticks = 0;

    @Override
    public void onTick() {
        if (ticks++ % 5 == 0) {
            if (!original.equals(copy)) {
                clear();
                copy = (T) original.copy();
                builder.accept(original,this);
            }
        }
        super.onTick();
    }
}
