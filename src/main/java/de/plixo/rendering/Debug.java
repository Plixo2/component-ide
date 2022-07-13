package de.plixo.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.glUseProgram;

public class Debug {
    public static void gizmo() {
        final float width = 0.04f;
        glDisable(GL_TEXTURE_2D);

        final boolean culled = glGetBoolean(GL_CULL_FACE);
        glDisable(GL_CULL_FACE);
        glColor4f(1f, 1f, 1f,1f);

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


    public static void cube(float size) {
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

}
