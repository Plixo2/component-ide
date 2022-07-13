package de.plixo.state;

import de.plixo.event.Dispatcher;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.*;
import de.plixo.game.AtlasGen;
import de.plixo.game.BlockMesh;
import de.plixo.game.MeshRegistry;
import de.plixo.game.impl.Simple;
import de.plixo.general.Tuple;
import de.plixo.general.dsa.Dsa;
import de.plixo.general.reference.ExposedReference;
import de.plixo.rendering.*;
import de.plixo.ui.elements.UIElement;
import de.plixo.ui.elements.layout.UICanvas;
import de.plixo.ui.general.FontRenderer;
import de.plixo.ui.general.LodestoneUI;
import de.plixo.ui.general.MainWindow;
import de.plixo.ui.impl.GLFWKeyboard;
import de.plixo.ui.impl.GLFWMouse;
import de.plixo.ui.impl.OpenGlRenderer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public static Window INSTANCE;

    @Getter()
    @Setter
    @Accessors(fluent = true)
    private long id;

    @Getter
    @Accessors(fluent = true)
    private int width;
    @Getter
    @Accessors(fluent = true)
    private int height;

    @Getter
    @Accessors(fluent = true)
    private Camera camera;

    MainWindow uiWindow;

    @Getter
    @Accessors(fluent = true)
    private Matrix4f projview = new Matrix4f();



    @SubscribeEvent
    void init(@NotNull InitEvent event) {
        id = initGlfw();

        camera = new Camera(0, 0, 6, false, new Vector3f(5.5f,5.5f,5.5f), 70);
        Dispatcher.register(camera);
    }

    @SubscribeEvent
    void postInit(@NotNull PostInitEvent event) {
        final FontRenderer verdana = new FontRenderer(new Font("Verdana", Font.BOLD, 18));
        OpenGlRenderer.setFontRenderer(verdana);
        new LodestoneUI(new OpenGlRenderer(), new GLFWKeyboard(), new GLFWMouse());

//        val config = Dsa.compileToShader("content/shader/test.toml");
//        shader = config.second;
//        projection = shader.uniform("projection");
//        view = shader.uniform("view");
//        model = shader.uniform("model");
//
//        float vertices[] = {
//                0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
//                0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom right
//                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom left
//                -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f  // top left
//        };
//        int indices[] = {  // note that we start from 0!
//                0, 1, 3,  // first Triangle
//                1, 2, 3   // second Triangle
//        };

//        mesh = Mesh.from_raw(vertices, indices,
//                new Shader.Attribute[]{Shader.Attribute.Vec3, Shader.Attribute.Vec3, Shader.Attribute.Vec2});

        Dispatcher.emit(new ResizeEvent(new Vector2i(width, height)));
    }

    @SubscribeEvent
    void renderEvent(@NotNull RenderEvent event) {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        final Matrix4f projectionMatrix = camera.projection();
        final Matrix4f viewMatrix = camera.view();

        glMatrixMode(GL_PROJECTION);
        try (final MemoryStack stack = stackPush()) {
            glLoadMatrixf(projectionMatrix.get(stack.mallocFloat(16)));
        }
        glMatrixMode(GL_MODELVIEW);
        try (final MemoryStack stack = stackPush()) {
            glLoadMatrixf(viewMatrix.get(stack.mallocFloat(16)));
        }



        projectionMatrix.mul(viewMatrix, projview);

        Debug.gizmo();

        Dispatcher.emit(new Render3DEvent(event.delta()));

        val dir_ = screenToWorld((float) IO.getMouse().x, (float) IO.getMouse().y);
        val origin = dir_.first;
        val dir = dir_.second;
        val forward = origin.add(dir.mul(10f));
        glPushMatrix();
        glTranslatef(forward.x, forward.y, forward.z);
        Debug.cube(0.1f);

        glPopMatrix();


        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glUseProgram(0);


        final Vector2f screen = worldToScreen(new Vector3f(1, 0, 0));
        glPushMatrix();
        glTranslatef(screen.x, screen.y, 0);
        glScaled(2, 2, 0);
        UIElement.GUI.drawCenteredString("X", 0, 0, 0xFFFF0000);
        glPopMatrix();

        final Vector2f screen2 = worldToScreen(new Vector3f(0, 1, 0));
        glPushMatrix();
        glTranslatef(screen2.x, screen2.y, 0);
        glScaled(2, 2, 0);
        UIElement.GUI.drawCenteredString("Y", 0, 0, 0xFF00FF00);
        glPopMatrix();

        final Vector2f screen3 = worldToScreen(new Vector3f(0, 0, 1));
        glPushMatrix();
        glTranslatef(screen3.x, screen3.y, 0);
        glScaled(2, 2, 0);
        UIElement.GUI.drawCenteredString("Z", 0, 0, 0xFF0000FF);
        glPopMatrix();

        UIElement.GUI.pushMatrix();
        UIElement.GUI.scale(2, 2);
        uiWindow.drawScreen((float) IO.getMouse().x / 2f, (float) IO.getMouse().y / 2f);
        UIElement.GUI.popMatrix();


        glfwSwapBuffers(id);
        glfwPollEvents();
    }

    void ui() {
        final MainWindow.Window win = MainWindow.displayWindow("Main");
        final UICanvas canvas = win.getCanvas();
        canvas.setColor(0);
        Dispatcher.emit(new UIInitEvent(canvas));
//        0xFF0D0D0D
    }

    public Vector2f worldToScreen(Vector3f position) {
        val vec4 = new Vector4f(position, 1.0f);
        final Matrix4f projectionMatrix = camera.projection();
        final Matrix4f viewMatrix = camera.view();
        final Vector4f mul = vec4.mul(projectionMatrix.mul(viewMatrix));

        val x = mul.x / mul.w;
        var y = mul.y / mul.w;
        y *= -1;
        final float screen_x = (width / 2.0f) + x * (width / 2.0f);
        final float screen_y = (height / 2.0f) + y * (height / 2.0f);
        return new Vector2f(screen_x, screen_y);
    }

    public Tuple<Vector3f, Vector3f> screenToWorld(float x, float y) {
        val xNDC = (2 * (x / width) - 1f);
        val yNDC = (-2 * (y / height) + 1f);
        val projectionMatrix = camera.projection();
        val viewMatrix = camera.view();
        val camera_inverse_matrix = (projectionMatrix.mul(viewMatrix)).invert();
        val near = (new Vector4f(xNDC, yNDC, 0, 1)).mul(camera_inverse_matrix);
        val far = (new Vector4f(xNDC, yNDC, 1, 1)).mul(camera_inverse_matrix);

        val near_ = new Vector3f(near.x, near.y, near.z).div(near.w);
        val far_ = new Vector3f(far.x, far.y, far.z).div(far.w);
        val dir = far_.sub(near_);
        val origin = new Vector3f(near_.x, near_.y, near_.z);

        return new Tuple<>(origin, dir.normalize());
    }

    @SubscribeEvent
    void tick(@NotNull TickEvent event) {
        uiWindow.onTick();


//        System.out.println(vector3f);
    }

    @SubscribeEvent
    void resize(@NotNull ResizeEvent event) {
        width = event.size().x;
        height = event.size().y;

        uiWindow = new MainWindow(width / 2, height / 2);
        ui();
    }

    private long initGlfw() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        val window = glfwCreateWindow(1280, 720, "", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        if (glfwRawMouseMotionSupported())
            glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);

        long cursor = glfwCreateStandardCursor(org.lwjgl.glfw.GLFW.GLFW_CROSSHAIR_CURSOR);
        glfwSetCursor(window, cursor);

        glfwSetKeyCallback(window, (window_, key, scancode, action, mods) -> {
            Dispatcher.emit(new KeyEvent(key, scancode, action, mods));
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window_, true);
        });
        ExposedReference<Vector2d> last = new ExposedReference<>(new Vector2d());
        glfwSetCursorPosCallback(window, (window_, x, y) -> {
            final Vector2d pos = new Vector2d(x, y);
            final Vector2d delta = new Vector2d(x, y).sub(last.value);
            Dispatcher.emit(new MouseMoveEvent(pos, delta));
            last.value = pos;
        });
        glfwSetFramebufferSizeCallback(window, ((window_, width, height) -> {
            Dispatcher.emit(new ResizeEvent(new Vector2i(width, height)));
        }));
        glfwSetCharCallback(window, ((window_, codepoint) -> {
            Dispatcher.emit(new CharEvent((char) codepoint));
        }));

        glfwSetMouseButtonCallback(window, ((window_, button, action, mods) -> {
            double[] x = {0};
            double[] y = {0};
            glfwGetCursorPos(window, x, y);
            if (action == GLFW_PRESS) {
                Dispatcher.emit(new MouseClickEvent(button, (float) x[0], (float) y[0]));
            } else {
                Dispatcher.emit(new MouseReleaseEvent(button, (float) x[0], (float) y[0]));
            }
        }));

        glfwSetScrollCallback(window, ((window_, x, y) -> {
            Dispatcher.emit(new ScrollEvent(new Vector2d(x, y)));
        }));


        try (MemoryStack stack = stackPush()) {
            final IntBuffer pWidth = stack.mallocInt(1);
            final IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            assert vidmode != null;
            width = pWidth.get(0);
            height = pHeight.get(0);
            glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        return window;
    }

    @SubscribeEvent
    void onClick(@NotNull MouseClickEvent event) {
        uiWindow.mouseClicked(event.mouseX() / 2f, event.mouseY() / 2f, event.button());
    }

    @SubscribeEvent
    void onRelease(@NotNull MouseReleaseEvent event) {
        uiWindow.mouseReleased(event.mouseX() / 2f, event.mouseY() / 2f, event.button());
    }

    @SubscribeEvent
    void key(@NotNull KeyEvent event) {
        if (event.action() == GLFW_PRESS)
            uiWindow.keyTyped((char) 0, event.key());
    }

    @SubscribeEvent
    void onChar(@NotNull CharEvent event) {
        uiWindow.keyTyped(event.character(), 0);
    }
}
