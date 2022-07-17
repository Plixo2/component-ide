package de.plixo.rendering;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.general.Color;
import de.plixo.rendering.targets.Shader;
import de.plixo.ui.lib.UI;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Debug {


    static List<TimeDelay> delays = new CopyOnWriteArrayList<>();

    static List<Point> points = new ArrayList<>();
    static List<Line> lines = new ArrayList<>();

    public static Vector3f offset = new Vector3f(0, 0, 0);

    static Shader pointShader;
    static Mesh pointMesh;

    static Shader lineShader;
    static Mesh lineMesh;

    @AllArgsConstructor
    private static class TimeDelay {
        @NotNull Runnable draw;
        float secondsLeft;
    }

    public static void drawLinedCube(@NotNull Vector3f b, @NotNull Color color) {
        drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 0, b.z), color);
        drawLine(new Vector3f(0, 0, 0), new Vector3f(0, b.y, 0), color);
        drawLine(new Vector3f(0, 0, 0), new Vector3f(b.x, 0, 0), color);
        drawLine(new Vector3f(0, b.y, 0), new Vector3f(0, b.y, b.z), color);
        drawLine(new Vector3f(0, b.y, 0), new Vector3f(b.x, b.y, 0), color);
        drawLine(new Vector3f(0, b.y, b.z), new Vector3f(b.x, b.y, b.z), color);
        drawLine(new Vector3f(b.x, b.y, 0), new Vector3f(b.x, b.y, b.z), color);
        drawLine(new Vector3f(0, 0, b.z), new Vector3f(b.x, 0, b.z), color);
        drawLine(new Vector3f(b.x, 0, 0), new Vector3f(b.x, 0, b.z), color);
        drawLine(new Vector3f(b.x, 0, 0), new Vector3f(b.x, b.y, 0), color);
        drawLine(new Vector3f(b.x, 0, b.z), new Vector3f(b.x, b.y, b.z), color);
        drawLine(new Vector3f(0, 0, b.z), new Vector3f(0, b.y, b.z), color);
    }

    public static void drawLine(@NotNull Vector3f a, @NotNull Vector3f b, @NotNull Color color) {
        final var a_ = new Vector3f(a).add(offset);
        final var b_ = new Vector3f(b).add(offset);
        lines.add(new Line(a_, b_, color));
    }

    public static void drawPoint(@NotNull Vector3f point, @NotNull Color color) {
        points.add(new Point(new Vector3f(point).add(offset), color));
    }

    public static void drawPoint(@NotNull Color color) {
        points.add(new Point(offset, color));
    }

    public static void drawPoint(float x, float y, float z, Color color) {
        drawPoint(new Vector3f(x, y, z), color);
    }


    public static void draw(@NotNull Runnable runnable, float seconds) {
        delays.add(new TimeDelay(runnable, seconds));
    }

    public static void draw(@NotNull Runnable runnable, float seconds, @Nullable Vector3f location) {
        if (location == null) {
            draw(runnable, seconds);
            return;
        }
        val location_ = new Vector3f(location);
        delays.add(new TimeDelay(() -> {
            offset = new Vector3f(location_.x, location_.y, location_.z);
            runnable.run();
            offset = new Vector3f(0, 0, 0);
        }, seconds));
    }

    static Shader.Uniform pointColor;
    static Shader.Uniform pointView;
    static Shader.Uniform lineOffset;
    static Shader.Uniform lineColor;
    static Shader.Uniform lineView;

    @SubscribeEvent
    static void draw(@NotNull PostInitEvent render3DEvent) {
        pointShader = Shader.fromDSA("debug/point.toml");
        pointMesh = Mesh.from_raw(new float[]{0, 0, 0}, new int[]{0}, new Shader.Attribute[]{Shader.Attribute.Vec3}, 0,
                null);

        lineShader = Shader.fromDSA("debug/line.toml");
        lineMesh = Mesh.from_raw(new float[]{0, 0, 0, 1, 0, 0}, new int[]{0, 1},
                new Shader.Attribute[]{Shader.Attribute.Vec3}, Mesh.DYNAMIC_DRAW, null);


        pointColor = pointShader.uniform("color");
        pointView = pointShader.uniform("projview");
        lineOffset = pointShader.uniform("offset");
        lineColor = lineShader.uniform("color");
        lineView = lineShader.uniform("projview");


        lineShader.seal();
        pointShader.seal();
    }

    @SubscribeEvent
    static void draw(@NotNull Render3DEvent render3DEvent) {
        delays.forEach(ref -> {
            ref.draw.run();
            ref.secondsLeft -= render3DEvent.delta();
            if (ref.secondsLeft <= 0) {
                delays.remove(ref);
            }
        });
        if (!UI.reflectBool("debug depth", true)) {
            glDisable(GL_DEPTH_TEST);
        }
        glEnable(GL_BLEND);


        float size = UI.reflectFloat("debug size", 5, 0, 10);
        glPointSize(size);
        glLineWidth(size);

        pointView.loadProjView();
        pointShader.flush();
        points.forEach(point -> {
            lineOffset.load(point.pos);
            pointColor.load(point.color);
            pointShader.flush();
            glBindVertexArray(pointMesh.vao());
            glDrawElements(GL_POINTS, 1, GL_UNSIGNED_INT, 0);
        });
        points.clear();


        lineView.loadProjView();
        lineShader.flush();
        lines.forEach(line -> {
            lineMesh.put_dynamic_data(new float[]{
                    line.from.x,
                    line.from.y,
                    line.from.z,
                    line.to.x,
                    line.to.y,
                    line.to.z
            });
            lineColor.load(line.color);
            lineShader.flush();
            glBindVertexArray(lineMesh.vao());
            glDrawElements(GL_LINES, 2, GL_UNSIGNED_INT, 0);
        });
        lines.clear();

    }


    record Point(Vector3f pos, Color color) {

    }

    static record Line(Vector3f from, Vector3f to, Color color) {

    }

}
