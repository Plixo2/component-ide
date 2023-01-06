package de.plixo.intermediate;

import de.plixo.ui.lib.elements.State;
import de.plixo.ui.lib.elements.layout.UICanvas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class UIFlagged<T extends Flagged> extends UICanvas {
    T original;
    BiConsumer<T,UICanvas> builder;

    public UIFlagged(@Nullable UICanvas canvas, @NotNull T original, @NotNull BiConsumer<T,UICanvas> builder) {
        this.original = original;
        this.builder = builder;
        clear();
        builder.accept(original,this);
        if (canvas != null) {
            canvas.add(this);
        }
    }

    public UIFlagged(T original, BiConsumer<T,UICanvas> builder) {
        this(null,original, builder);
    }

    int ticks = 0;

    @Override
    public void onTick() {
        if (ticks++ % 5 == 0) {
            if (original.isDirty()) {
                original.cleanFlag();
                clear();
                builder.accept(original,this);
            }
        }
        super.onTick();
    }
}
