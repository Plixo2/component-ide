package de.plixo.game;

import de.plixo.general.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class BlockMesh {

    @Getter
    @Accessors(fluent = true)
    private @NotNull Color color;

    @Getter
    @Accessors(fluent = true)
    private @NotNull RenderObj[] objs;

}
