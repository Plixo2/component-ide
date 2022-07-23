package de.plixo.systems;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.game.item.Item;
import de.plixo.impl.item.IronOre;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class ItemSystem {

    Map<String, Item> items = new HashMap<>();

    @SubscribeEvent
    void init(@NotNull PostInitEvent event) {
        registerItem(new IronOre());
    }

    private void registerItem(@NotNull Item item) {
        items.put(item.localName(), item);
    }

    @SubscribeEvent
    void render(@NotNull Render3DEvent event) {

        items.forEach((k, item) -> {
            final var shader = item.shader();
            assert shader != null;
            shader.uniform("projview").loadProjView();
            shader.uniform("model").loadIdentity();
            shader.flush();
            item.meshes().forEach(mesh -> {
                final var texture = mesh.texture();
                if (texture != null) {
                    glEnable(GL_TEXTURE_2D);
                    glActiveTexture(GL_TEXTURE0);
                    texture.bind();
                } else {
                    glDisable(GL_TEXTURE_2D);
                }
                mesh.mesh().render();
            });
        });
    }
}
