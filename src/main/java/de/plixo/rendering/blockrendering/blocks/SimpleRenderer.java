package de.plixo.rendering.blockrendering.blocks;

import de.plixo.game.BlockMesh;
import de.plixo.game.impl.Simple;
import de.plixo.rendering.Debug;
import de.plixo.rendering.Shader;
import de.plixo.rendering.blockrendering.BlockRenderer;
import de.plixo.state.Window;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class SimpleRenderer extends BlockRenderer<Simple> {

    final Shader.Uniform projview;
    final Shader.Uniform model;

    public SimpleRenderer(Shader shader) {
        projview = shader.uniform("projview");
        model = shader.uniform("model");
    }

    @Override
    public void draw(@NotNull Simple block, @NotNull BlockMesh blockMesh) {
        projview.load(Window.INSTANCE.projview());
        final Matrix4f t = new Matrix4f();
        t.translate(block.x + 0.5f, block.y + 0.5f, block.z + 0.5f);
        model.load(t);
        glPushMatrix();
        glTranslatef(block.x, block.y, block.z);
        Debug.gizmo();
        glPopMatrix();

        super.draw(block, blockMesh);
    }
}