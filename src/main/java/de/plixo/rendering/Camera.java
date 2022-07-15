package de.plixo.rendering;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseMoveEvent;
import de.plixo.event.impl.RenderEvent;
import de.plixo.event.impl.ScrollEvent;
import de.plixo.general.Util;
import de.plixo.state.IO;
import de.plixo.state.Window;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@AllArgsConstructor
public class Camera {
    double yaw;
    double pitch;

    @Getter
    @Setter
    @Accessors(fluent = true)
    float distance;
    boolean perspective;

    @Getter
    @Setter
    @Accessors(fluent = true)
    Vector3f origin;
    double fov;


    public void rotate(double yaw, double pitch) {

        this.pitch = Util.clampDouble((this.pitch + pitch),89.9f,-89.9f);
//        this.yaw = Util.clampDouble((this.yaw + yaw),90,0);
        this.yaw += yaw;
    }


    public Matrix4f view() {
        final Matrix4f view = new Matrix4f();
        final Vector3f position = position();
        view.lookAt(position, origin, new Vector3f(0, 1, 0));
        return view;
    }

    public Matrix4f projection() {
        final Matrix4f projection = new Matrix4f();
        float aspect = Window.INSTANCE.width() / (float) Window.INSTANCE.height();
        projection.perspective((float) Math.toRadians(fov), aspect, 0.01f, 10000f, true);
        return projection;
    }

    public Matrix4f orthographic() {
        final Matrix4f projection = new Matrix4f();
        float aspect = Window.INSTANCE.width() / (float) Window.INSTANCE.height();
        float size = distance/2.0f;
        projection.ortho(-size * aspect, size  * aspect, -size, size, 0.001f, 1000f, true);
        return projection;
    }


    public Vector3f position() {
        final var yaw = Math.toRadians(this.yaw);
        final var pitch = Math.toRadians(this.pitch);
        final double x = Math.cos(yaw) * Math.cos(pitch);
        final double z = Math.sin(yaw) * Math.cos(pitch);
        final double y = Math.sin(pitch);
        return new Vector3f(origin).sub((float) (x * distance), (float) (y * distance),
                (float) (z * distance));
    }

    public Vector3f forward() {
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
        if (IO.mouseDown(1))
            rotate(event.delta().x * speed, -event.delta().y * speed);
    }

    @SubscribeEvent
    void scroll(@NotNull ScrollEvent event) {
        distance =  Util.clampFloat((float) (distance -  event.delta().y * 1.7f), 30,2);
    }

}
