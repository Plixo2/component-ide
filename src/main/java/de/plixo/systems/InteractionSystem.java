package de.plixo.systems;

import de.plixo.event.AssetServer;
import de.plixo.event.Dispatcher;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseClickEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.event.impl.WorldHoverEvent;
import de.plixo.event.impl.blocks.BlockInteractEvent;
import de.plixo.event.impl.blocks.PostBlockPlacEvent;
import de.plixo.event.impl.blocks.PreBlockPlaceEvent;
import de.plixo.general.IO;
import de.plixo.general.collision.EnumFacing;
import de.plixo.general.collision.RayCasting;
import de.plixo.general.collision.RayTraceBlockHit;
import de.plixo.general.collision.RayTraceHit;
import de.plixo.impl.block.Pipe;
import de.plixo.rendering.MeshBundle;
import de.plixo.state.Assets;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;


public class InteractionSystem {

    MeshBundle cursor;

    @SubscribeEvent
    void init(@NotNull PostInitEvent event) throws IOException {
        cursor = MeshBundle.generate("cursor.obj", "cursor.mtl", "obj.toml", 0).first;
    }

    @SubscribeEvent
    void place(@NotNull PreBlockPlaceEvent event) {
        val position = event.position();
        final var renderStorage = AssetServer.get(Assets.RenderStorage.class);
        val empty = renderStorage.get(position);
        if (empty == null) {
            val block = new Pipe();
            renderStorage.insert(block, position);
            Dispatcher.emit(new PostBlockPlacEvent(block));
        }
    }

    @SubscribeEvent
    void click(@NotNull MouseClickEvent event) {
        val mouse = RayCasting.raycast_camera();
        if (mouse instanceof RayTraceBlockHit hit) {
            if (event.button() == 0) {
                val origin = hit.block().veci();
                val normal = hit.face().normal();
                val target = new Vector3i(origin).add(normal);
                if (IO.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    Dispatcher.emit(new PreBlockPlaceEvent(target));
                } else if (!hit.block().interact()) {
                    Dispatcher.emit(new PreBlockPlaceEvent(target));
                } else {
                    Dispatcher.emit(new BlockInteractEvent(hit.block()));
                }
            } else if (event.button() == 1) {

            }
        } else if (mouse instanceof RayTraceHit hit) {
            if (event.button() == 0) {
                val origin = hit.position();
                val normal = new Vector3f(hit.face().normal()).mul(0.5f);
                val target = new Vector3f(origin).add(normal);
                val pos = new Vector3i(target, RoundingMode.TRUNCATE);
                Dispatcher.emit(new PreBlockPlaceEvent(pos));
            }
        }


    }

    @SubscribeEvent
    void update(@NotNull Render3DEvent event) {
        final var shader = cursor.commonShader();
        assert shader != null;

        shader.uniform("projview").loadProjView();
        val mouse = RayCasting.raycast_camera();
        val translate = new Matrix4f();

        var vec = new Vector3i(-1);

        if (mouse instanceof RayTraceBlockHit hit) {
            {
                val origin = hit.block().veci();
                val normal = hit.face().normal();
                vec = new Vector3i(origin).add(normal);
            }
            val origin = hit.block().veci();
            val normal = new Vector3f().mul(1f);
            val target = new Vector3f(origin).add(normal);
            val pos = new Vector3i(target, RoundingMode.TRUNCATE);
            val tar = new Vector3f(pos).add(0.5f, 0.5f, 0.5f).sub(new Vector3f(hit.face().normal()).mul(-0.5f));
            rotateForFace(translate, tar, hit.face());
        } else if (mouse instanceof RayTraceHit hit) {
            {
                val origin = hit.position();
                val normal = new Vector3f(hit.face().normal()).mul(0.5f);
                val target = new Vector3f(origin).add(normal);
                vec = new Vector3i(target, RoundingMode.TRUNCATE);
            }
            val origin = hit.position();
            val normal = new Vector3f(hit.face().normal()).mul(0.5f);
            val target = new Vector3f(origin).add(normal);
            val pos = new Vector3i(target, RoundingMode.TRUNCATE);
            val tar = new Vector3f(pos).add(0.5f, 0.5f, 0.5f).sub(normal);
            rotateForFace(translate, tar, hit.face());
        }
        Dispatcher.emit(new WorldHoverEvent(mouse, vec));

        shader.uniform("model").load(translate);
        cursor.render();
    }

    private void rotateForFace(Matrix4f translate, Vector3f tar, EnumFacing face) {
        switch (face) {
            case WEST, EAST -> {
                translate.rotateZ((float) (Math.PI / 2f));
            }
            case SOUTH, NORTH -> {
                translate.rotateX((float) (Math.PI / 2f));
            }
        }
        translate.setTranslation(tar);
    }

}
