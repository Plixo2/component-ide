package de.plixo;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseEvent;
import de.plixo.event.impl.ScrollEvent;
import de.plixo.event.impl.RenderEvent;
import de.plixo.state.IO;
import de.plixo.state.Window;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@AllArgsConstructor
public class Camera {
    double yaw;
    double pitch;
    double distance;
    boolean perspective;
    Vector3f origin;
    double fov;


    public void rotate(double yaw, double pitch) {
        this.yaw += yaw;
        this.pitch += pitch;
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
        projection.perspective((float) Math.toRadians(fov),aspect,0.01f,10000f,true);
        return projection;
    }

    public Matrix4f orthographic() {
        final Matrix4f projection = new Matrix4f();
        projection.ortho(-1, 1, -1, 1, 0.001f, 1000f, true);
        return projection;
    }


    public Vector3f position() {
        final double x = Math.cos(yaw) * Math.cos(pitch);
        final double z = Math.sin(yaw) * Math.cos(pitch);
        final double y = Math.sin(pitch);
        return new Vector3f(origin).sub((float) (x * distance), (float) (y * distance),
                (float) (z * distance));
    }

    public Vector3f forward() {
        final double x = Math.cos(yaw) * Math.cos(pitch);
        final double z = Math.sin(yaw) * Math.cos(pitch);
        final double y = Math.sin(pitch);
        return new Vector3f((float) -(x * distance), (float) -(y * distance), (float) -(z * distance));
    }


    @SubscribeEvent
    void rotate(@NotNull MouseEvent event) {
        float speed = 0.007f;
        if (IO.mouseDown(1)) rotate(event.delta().x * speed, -event.delta().y * speed);
    }

    @SubscribeEvent
    void scroll(@NotNull ScrollEvent event) {
        distance -= event.delta().y * 0.3f;
    }

    @SubscribeEvent
    void tick(@NotNull RenderEvent event) {
//        rotate(10 * event.delta(),0);
    }

}
