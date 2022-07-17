package de.plixo.game;

import de.plixo.rendering.Mesh;
import de.plixo.rendering.targets.Shader;
import de.plixo.rendering.targets.Texture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class RenderObj {
    @Getter
    @Accessors(fluent = true)
    private @NotNull Shader shader;

    @Getter
    @Accessors(fluent = true)
    private @Nullable Texture texture;
    @Getter
    @Accessors(fluent = true)
    private @NotNull Mesh mesh;
}
