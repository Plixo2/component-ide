package de.plixo.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.glUseProgram;

public class Debug {
    public static void renderGizmo() {
        final float width = 0.04f;

        final boolean culled = glGetBoolean(GL_CULL_FACE);
        glDisable(GL_CULL_FACE);

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

    public static void cube() {
        glBegin(GL_QUADS);
        glColor3f(0.0f, 0.0f, 0.2f);
        glVertex3f(0.5f, -0.5f, -0.5f);
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f(-0.5f, 0.5f, -0.5f);
        glVertex3f(0.5f, 0.5f, -0.5f);
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(0.5f, -0.5f, 0.5f);
        glVertex3f(0.5f, 0.5f, 0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glVertex3f(-0.5f, -0.5f, 0.5f);
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, 0.5f, -0.5f);
        glVertex3f(0.5f, 0.5f, 0.5f);
        glVertex3f(0.5f, -0.5f, 0.5f);
        glColor3f(0.2f, 0.0f, 0.0f);
        glVertex3f(-0.5f, -0.5f, 0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glVertex3f(-0.5f, 0.5f, -0.5f);
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(0.5f, 0.5f, 0.5f);
        glVertex3f(0.5f, 0.5f, -0.5f);
        glVertex3f(-0.5f, 0.5f, -0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glColor3f(0.0f, 0.2f, 0.0f);
        glVertex3f(0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, -0.5f, 0.5f);
        glVertex3f(-0.5f, -0.5f, 0.5f);
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glEnd();
    }

}
