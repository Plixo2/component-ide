package de.plixo.rendering;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseMoveEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.event.impl.ScrollEvent;
import de.plixo.general.Util;
import de.plixo.general.IO;
import de.plixo.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;


public class Camera {

    @Getter
    @Setter
    @Accessors(fluent = true)
    private float yaw;
    private float pitch;

    @Getter
    @Setter
    @Accessors(fluent = true)
    private float distance = 6;


    private Vector3f origin = new Vector3f(5f, 5f, 5f);

    private double fov = 70;

    @Getter
    @Setter
    @Accessors(fluent = true)
    boolean perspective = false;

    @Getter
    @Accessors(fluent = true)
    private float xOffset = 0;

    @Getter
    @Accessors(fluent = true)
    private float yOffset = 0;

    public void rotate(float yaw, float pitch) {

        this.pitch = Util.clampFloat((this.pitch + pitch), 89.9f, -89.9f);
        this.yaw += yaw;
    }


    public Matrix4f view() {
        final Matrix4f view = new Matrix4f();
        final Vector3f position = position();
        view.lookAt(position, origin(), new Vector3f(0, 1, 0));
        return view;
    }

    public Matrix4f projection() {
        if (!perspective) {
            return orthographic();
        }
        final Matrix4f projection = new Matrix4f();
        float aspect = RenderSystem.INSTANCE.width() / (float) RenderSystem.INSTANCE.height();
        projection.perspective((float) Math.toRadians(fov), aspect, 0.01f, 10000f, true);
        return projection;
    }

    private Matrix4f orthographic() {
        final Matrix4f projection = new Matrix4f();
        float aspect = RenderSystem.INSTANCE.width() / (float) RenderSystem.INSTANCE.height();
        float size = distance / 2.0f;

        projection.ortho(-size * aspect * 1, size * aspect, -size, size,
                0.001f, 1000f,
                true);
        return projection;
    }


    public @NotNull Vector3f position() {
        final var yaw = Math.toRadians(this.yaw);
        final var pitch = Math.toRadians(this.pitch);

        final double x = Math.cos(yaw) * Math.cos(pitch);
        final double z = Math.sin(yaw) * Math.cos(pitch);
        final double y = Math.sin(pitch);

        float distance = perspective ? this.distance : 30;
        return new Vector3f(origin()).sub((float) (x * distance), (float) (y * distance),
                (float) (z * distance));
    }

    public Vector3f origin() {
        final float xL = (float) Math.cos(Math.toRadians(yaw + 90)) * -xOffset;
        final float zL = (float) Math.sin(Math.toRadians(yaw + 90)) * -xOffset;
        return new Vector3f(origin).add(xL, yOffset, zL);
    }

    public @NotNull Vector3f forward() {
        final var yaw = Math.toRadians(this.yaw);
        final var pitch = Math.toRadians(this.pitch);
        final double x = Math.cos(yaw) * Math.cos(pitch);
        final double z = Math.sin(yaw) * Math.cos(pitch);
        final double y = Math.sin(pitch);
        return new Vector3f((float) -(x * distance), (float) -(y * distance), (float) -(z * distance));
    }


    @SubscribeEvent
    void rotate(@NotNull MouseMoveEvent event) {
        float speed = 0.25f;
        if (IO.mouseDown(1)) {
            rotate((float) (event.delta().x * speed), (float) (-event.delta().y * speed));
        } else if (IO.mouseDown(2)) {
            xOffset += event.delta().x * 0.001f * this.distance;
            yOffset += event.delta().y * 0.001f * this.distance;
            Debug.draw(this::drawCameraRing, 4f);
        }
    }

    public void xOffset(float val) {
        this.xOffset = val;
        drawCameraRing();
    }

    public void yOffset(float val) {
        this.yOffset = val;
        drawCameraRing();
    }

    @SubscribeEvent
    void scroll(@NotNull ScrollEvent event) {
        distance = Util.clampFloat((float) (distance - event.delta().y * 1.7f), 30, 2);
    }

    @SubscribeEvent
    void drawCenter(@NotNull Render3DEvent event) {
    }

    void drawCameraRing() {
//        float x = 0;
//        while (x <= Math.PI * 2) {
//            final var pos = new Vector3f((float) Math.sin(x) * xOffset, yOffset, (float) Math.cos(x) * xOffset);
//            final var end = new Vector3f((float) Math.sin(x + 0.1f) * xOffset, yOffset,
//                    (float) Math.cos(x + 0.1f) * xOffset);
//            Debug.drawLine(pos.add(origin), end.add(origin), Color.RED);
//            x += 0.1f;
//        }
    }
}
