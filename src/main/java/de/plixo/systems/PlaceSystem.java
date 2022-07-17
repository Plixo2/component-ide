package de.plixo.systems;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseClickEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.game.RenderObj;
import de.plixo.game.blocks.Wool;
import de.plixo.game.collision.*;
import de.plixo.general.Color;
import de.plixo.general.IO;
import de.plixo.rendering.Debug;
import de.plixo.rendering.MeshBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.IOException;
import java.util.Objects;


public class PlaceSystem {
    public static PlaceSystem INSTANCE;

    @Getter
    @Accessors(fluent = true)
    @NotNull RayTraceResult mouse = new RayTraceMiss();

    MeshBundle cursor;

    @SubscribeEvent
    void init(@NotNull PostInitEvent event) throws IOException {
        cursor = MeshBundle.generate("cursor.obj", "cursor.mtl", "obj.toml", 0).first;
    }

    @SubscribeEvent
    void click(@NotNull MouseClickEvent event) {
        if (event.button() == 0) {
            if (mouse instanceof RayTraceBlockHit hit) {
                final var origin = hit.block().block().veci();
                final var normal = hit.face().normal();
                final var target = new Vector3i(origin).add(normal);
                WorldSystem.INSTANCE.insert(new Wool(), target);
            } else if (mouse instanceof RayTraceHit hit) {
                final var origin = hit.position();
                final var normal = new Vector3f(hit.face().normal()).mul(0.5f);
                final var target = new Vector3f(origin).add(normal);
                final var pos = new Vector3i(target, RoundingMode.TRUNCATE);
                WorldSystem.INSTANCE.insert(new Wool(), pos);
            }
        }

    }

    @SubscribeEvent
    void update(@NotNull Render3DEvent event) {
        cursor.commonShader().uniform("projview").loadProjView();
        final var translate = new Matrix4f();

        val dir_ = RayCasting.screenToWorld(IO.getMouse().x, IO.getMouse().y);
        val forward = new Vector3f(dir_.first).add(new Vector3f(dir_.second).mul(1000f));
        final var result = RayCasting.rayCastWorld(dir_.first, forward);

        this.mouse = result;
        if (result instanceof RayTraceMiss) {

        } else if (mouse instanceof RayTraceBlockHit hit) {
            for (RenderObj obj : hit.block().mesh().objs()) {
                final var aabb =
                        Objects.requireNonNull(obj.mesh().collision()).boundingBox().offset(hit.block().block().x() + 0.5f,
                                hit.block().block().y() + 0.5f,hit.block().block().z() + 0.5f);
                Debug.offset = new Vector3f(aabb.minX, aabb.minY, aabb.minZ);
                Debug.drawLinedCube(new Vector3f(aabb.width(), aabb.height(), aabb.depth()), Color.BLACK);
                Debug.offset = new Vector3f(0, 0, 0);
            }

            final var origin = hit.block().block().veci();
            final var normal = new Vector3f().mul(1f);
            final var target = new Vector3f(origin).add(normal);
            final var pos = new Vector3i(target, RoundingMode.TRUNCATE);
            final var tar = new Vector3f(pos).add(0.5f, 0.5f, 0.5f).sub(new Vector3f(hit.face().normal()).mul(-0.5f));

            switch (hit.face()) {
                case WEST, EAST -> {
                    translate.rotateZ((float) (Math.PI / 2f));
                }
                case SOUTH, NORTH -> {
                    translate.rotateX((float) (Math.PI / 2f));
                }
            }
            translate.setTranslation(tar);
        } else if (mouse instanceof RayTraceHit hit) {
            final var origin = hit.position();
            final var normal = new Vector3f(hit.face().normal()).mul(0.5f);
            final var target = new Vector3f(origin).add(normal);
            final var pos = new Vector3i(target, RoundingMode.TRUNCATE);
            final var tar = new Vector3f(pos).add(0.5f, 0.5f, 0.5f).sub(normal);

            switch (hit.face()) {
                case WEST, EAST -> {
                    translate.rotateZ((float) (Math.PI / 2f));
                }
                case SOUTH, NORTH -> {
                    translate.rotateX((float) (Math.PI / 2f));
                }
            }
            translate.setTranslation(tar);
        }


        cursor.commonShader().uniform("model").load(translate);
        cursor.render();


    }
}
