package de.plixo.systems;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseClickEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.impl.block.Chest;
import de.plixo.impl.block.Wool;
import de.plixo.general.IO;
import de.plixo.general.collision.*;
import de.plixo.rendering.MeshBundle;
import de.plixo.state.UIState;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.IOException;


public class InteractionSystem {
    public static InteractionSystem INSTANCE;

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
                switch (UIState.action) {
                    case PLACE -> {
                        val origin = hit.block().veci();
                        val normal = hit.face().normal();
                        val target = new Vector3i(origin).add(normal);
                        WorldSystem.INSTANCE.insert(new Chest(), target);
                    }
                    case EDIT -> {
                        val block = hit.block();
                        block.interact();
                    }
                    case INSPECT -> {
                    }
                }
            } else if (mouse instanceof RayTraceHit hit) {
                switch (UIState.action) {
                    case PLACE -> {
                        val origin = hit.position();
                        val normal = new Vector3f(hit.face().normal()).mul(0.5f);
                        val target = new Vector3f(origin).add(normal);
                        val pos = new Vector3i(target, RoundingMode.TRUNCATE);
                        WorldSystem.INSTANCE.insert(new Chest(), pos);
                    }
                    case EDIT -> {
                    }
                    case INSPECT -> {
                    }
                }
            }

        }

    }

    @SubscribeEvent
    void update(@NotNull Render3DEvent event) {
        cursor.commonShader().uniform("projview").loadProjView();
        val translate = new Matrix4f();
        val dir_ = RayCasting.screenToWorld(IO.getCanvasMouse().x, IO.getCanvasMouse().y);
        val forward = new Vector3f(dir_.first).add(new Vector3f(dir_.second).mul(1000f));
        val result = RayCasting.rayCastWorld(dir_.first, forward);

        this.mouse = result;
        if (result instanceof RayTraceMiss) {

        } else if (mouse instanceof RayTraceBlockHit hit) {
            val origin = hit.block().veci();
            val normal = new Vector3f().mul(1f);
            val target = new Vector3f(origin).add(normal);
            val pos = new Vector3i(target, RoundingMode.TRUNCATE);
            val tar = new Vector3f(pos).add(0.5f, 0.5f, 0.5f).sub(new Vector3f(hit.face().normal()).mul(-0.5f));

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
            val origin = hit.position();
            val normal = new Vector3f(hit.face().normal()).mul(0.5f);
            val target = new Vector3f(origin).add(normal);
            val pos = new Vector3i(target, RoundingMode.TRUNCATE);
            val tar = new Vector3f(pos).add(0.5f, 0.5f, 0.5f).sub(normal);

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
