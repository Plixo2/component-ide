package de.plixo.rendering;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.general.Color;
import de.plixo.general.reference.ObjectReference;
import de.plixo.ui.elements.resources.UIColor;
import de.plixo.ui.elements.resources.UIFillBar;
import de.plixo.ui.impl.UI;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.glUseProgram;

public class Debug {


    static List<TimeDelay> delays = new CopyOnWriteArrayList<>();

    @AllArgsConstructor
    static
    class TimeDelay {
        @NotNull Runnable draw;
        float secondsLeft;
    }

    public static void gizmo() {
        final float width = 0.04f;
        glDisable(GL_TEXTURE_2D);

        final boolean culled = glGetBoolean(GL_CULL_FACE);
        glDisable(GL_CULL_FACE);
        glColor4f(1f, 1f, 1f, 1f);

        glUseProgram(0);

        glBegin(GL_QUADS);
        glColor3f(1f, 0.0f, 0.0f);
        glVertex3f(0, -width, 0);
        glVertex3f(1, -width, 0);
        glVertex3f(1, width, 0);
        glVertex3f(0, width, 0);

        glVertex3f(0, 0, -width);
        glVertex3f(1, 0, -width);
        glVertex3f(1, 0, width);
        glVertex3f(0, 0, width);

        glColor3f(0f, 1.0f, 0f);
        glVertex3f(0, 0, -width);
        glVertex3f(0, 1, -width);
        glVertex3f(0, 1, width);
        glVertex3f(0, 0, width);

        glVertex3f(-width, 0, 0);
        glVertex3f(-width, 1, 0);
        glVertex3f(width, 1, 0);
        glVertex3f(width, 0, 0);


        glColor3f(0f, 0.0f, 1f);
        glVertex3f(0, -width, 0);
        glVertex3f(0, -width, 1);
        glVertex3f(0, width, 1);
        glVertex3f(0, width, 0);

        glVertex3f(-width, 0, 0);
        glVertex3f(-width, 0, 1);
        glVertex3f(width, 0, 1);
        glVertex3f(width, 0, 0);

        glEnd();

        if (culled) {
            glEnable(GL_CULL_FACE);
        }
    }

    public static void linedCube(float x, float y, float z) {
        glUseProgram(0);
        glDisable(GL_TEXTURE_2D);

        val c_ = UI.reflectColor(21, "Line Color", new Color(0xFF00FF00));
        c_.bindGl();

        if(!UI.reflectBool(26,"depth",true)) {
            glDisable(GL_DEPTH_TEST);
        } else {
            glEnable(GL_DEPTH_TEST);
        }

        float width = UI.reflectFloat(22, "Line Width", 3f, 0,10);

        final boolean culled = glGetBoolean(GL_DEPTH_TEST);

        glLineWidth(width);
        glBegin(GL_LINE_STRIP);

        glVertex3f(0, 0, 0);
        glVertex3f(x, 0, 0);
        glVertex3f(x, y, 0);
        glVertex3f(0, y, 0);
        glVertex3f(0, 0, 0);

        glVertex3f(0, 0, z);
        glVertex3f(x, 0, z);
        glVertex3f(x, y, z);
        glVertex3f(0, y, z);
        glVertex3f(0, 0, z);

        glEnd();

        glBegin(GL_LINES);

        glVertex3f(x, 0, 0);
        glVertex3f(x, 0, z);

        glVertex3f(x, y, 0);
        glVertex3f(x, y, z);

        glVertex3f(0, y, 0);
        glVertex3f(0, y, z);

        glEnd();
        if (culled) {
            glEnable(GL_DEPTH_TEST);
        }
    }

    public static void cube(float size) {
        glEnable(GL_DEPTH_TEST);
        glUseProgram(0);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glColor3f(0.0f, 0.0f, 0.2f);
        glVertex3f(size, -size, -size);
        glVertex3f(-size, -size, -size);
        glVertex3f(-size, size, -size);
        glVertex3f(size, size, -size);
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(size, -size, size);
        glVertex3f(size, size, size);
        glVertex3f(-size, size, size);
        glVertex3f(-size, -size, size);
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(size, -size, -size);
        glVertex3f(size, size, -size);
        glVertex3f(size, size, size);
        glVertex3f(size, -size, size);
        glColor3f(0.2f, 0.0f, 0.0f);
        glVertex3f(-size, -size, size);
        glVertex3f(-size, size, size);
        glVertex3f(-size, size, -size);
        glVertex3f(-size, -size, -size);
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(size, size, size);
        glVertex3f(size, size, -size);
        glVertex3f(-size, size, -size);
        glVertex3f(-size, size, size);
        glColor3f(0.0f, 0.2f, 0.0f);
        glVertex3f(size, -size, -size);
        glVertex3f(size, -size, size);
        glVertex3f(-size, -size, size);
        glVertex3f(-size, -size, -size);
        glEnd();
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
            glPushMatrix();
            glTranslatef(location_.x, location_.y, location_.z);
            runnable.run();
            glPopMatrix();
        }, seconds));
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
    }


}
