package de.plixo.impl.render;

import de.plixo.impl.block.Pipe;
import de.plixo.rendering.BlockRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class DefaultRenderer extends BlockRenderer<Pipe> {

    @Override
    public void draw(@NotNull Pipe block, @NotNull Matrix4f projection) {
        final var shader = block.getShader();
        shader.uniform("projview").load(projection);
        final Matrix4f t = new Matrix4f();
        t.translate(block.x() + 0.5f, block.y() + 0.5f, block.z() + 0.5f);
        shader.uniform("model").load(t);
        shader.flush();
        super.draw(block,projection);
    }
}
