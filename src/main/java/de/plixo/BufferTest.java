package de.plixo;

import de.plixo.game.meta.MetaTest;
import de.plixo.general.FileUtil;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class BufferTest {
    public static void main(String[] args) throws ReflectiveOperationException {

        val file = new File("saves/test.txt");
        FileUtil.makeFile(file);

        MetaTest list = new MetaTest();
        FileUtil.writeObj(list,file);


        final var dummy = new MetaTest();
        FileUtil.loadObj(dummy,file);


        System.out.println(dummy.items);


//        val l = MainHook.initGlfw();
//        GL.createCapabilities();
//        int w = 0;
//        int h = 0;
//        try (MemoryStack stack = stackPush()) {
//            final IntBuffer pWidth = stack.mallocInt(1);
//            final IntBuffer pHeight = stack.mallocInt(1);
//            glfwGetWindowSize(l, pWidth, pHeight);
//            final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//            assert vidmode != null;
//            val width1 = pWidth.get(0);
//            val height1 = pHeight.get(0);
//            w = width1;
//            h = height1;
//            glfwSetWindowPos(l, (vidmode.width() - width1) / 2,
//                    (vidmode.height() - height1) / 2);
//        }
//
//        Framebuffer framebuffer = Framebuffer.generate(w/2,h/2);
//        Texture texture = Texture.createAttachment(w/2,h/2);
//        framebuffer.attach_texture(texture,GL_COLOR_ATTACHMENT0);
//
//        float quadVertices[] = {
//                -1.0f,  1.0f,  0.0f, 1.0f,
//                -1.0f, -1.0f,  0.0f, 0.0f,
//                1.0f, -1.0f,  1.0f, 0.0f,
//
//                -1.0f,  1.0f,  0.0f, 1.0f,
//                1.0f, -1.0f,  1.0f, 0.0f,
//                1.0f,  1.0f,  1.0f, 1.0f
//        };
//        val layout = new Shader.Attribute[]{Shader.Attribute.Vec2, Shader.Attribute.Vec2};
//        Mesh mesh = Mesh.from_raw(quadVertices,new int[]{0,1,2,3,4,5}, layout,0,null);
//        Shader shader = Dsa.compileToShader("fullscreen.toml").second;
//
//        Texture texture1 = Texture.fromFile("content/atlas.png",null);
//
//        while (!glfwWindowShouldClose(l)) {
//            glViewport(0, 0, w, h);
//
//            glMatrixMode(GL_PROJECTION);
//            glLoadIdentity();
//            glOrtho(0, w, 0, h, -1, 1);
//            glMatrixMode(GL_MODELVIEW);
//            glLoadIdentity();
//            glDisable(GL_CULL_FACE);
//            glUseProgram(0);
//
//            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//            glEnable(GL_BLEND);
//
//
//            framebuffer.unbind();
//            framebuffer.bind();
//            framebuffer.clear();
//            texture1.drawStatic(0,0,w,h,0,0,1,1,-1);
//
//            drawRect(0,0,100,100,ColorLib.mainColor);
//            framebuffer.unbind();
//
//
//            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//            glClear(GL_COLOR_BUFFER_BIT);
//
////            glEnable(GL_TEXTURE_2D);
////            shader.flush();
////            texture.bind();
////            mesh.drawElements();
//
//            texture.drawStatic(0,0,w,h,0,0,1,1,-1);
//
//
//            glfwSwapInterval(1);
//            glfwSwapBuffers(l);
//            glfwPollEvents();
//        }
    }

//    public static void drawRect(float left, float top, float right, float bottom, Color color) {
//
//        glDisable(GL_TEXTURE_2D);
//        color.bindGl();
//        glBegin(GL_QUADS);
//        glVertex2d(left, bottom);
//        glVertex2d(right, bottom);
//        glVertex2d(right, top);
//        glVertex2d(left, top);
//        glEnd();
//    }
}