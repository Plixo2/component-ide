package de.plixo.impl.render;

import de.plixo.impl.block.Simple;
import de.plixo.rendering.MeshBundle;
import de.plixo.rendering.BlockRenderer;
import de.plixo.rendering.targets.Shader;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class SimpleRenderer extends BlockRenderer<Simple> {

    final Shader.Uniform projview;
    final Shader.Uniform model;


    public SimpleRenderer(Shader shader) {
        projview = shader.uniform("projview");
        model = shader.uniform("model");
    }

    @Override
    public void draw(@NotNull Simple block, @NotNull MeshBundle meshBundle, @NotNull Matrix4f projection) {
        projview.load(projection);
        final Matrix4f t = new Matrix4f();
        t.translate(block.x() + 0.5f, block.y() + 0.5f, block.z() + 0.5f);
        model.load(t);
//        glPushMatrix();
//        glTranslatef(block.x, block.y, block.z);
//        Debug.gizmo();
//        glPopMatrix();

        super.draw(block, meshBundle, projection);
    }
}
