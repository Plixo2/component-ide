package de.plixo.ui;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.UIChildEvent;
import de.plixo.game.Block;
import de.plixo.rendering.MeshBundle;
import de.plixo.rendering.BlockRenderer;
import de.plixo.state.UIState;
import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.impl.ui.UITexture;
import de.plixo.ui.lib.elements.layout.UIDraggable;
import de.plixo.ui.lib.general.ColorLib;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlacePanel {


    static List<Block2DEntry<?>> list = new ArrayList<>();

    @AllArgsConstructor
    static class Block2DEntry<T extends Block> {
        BlockRenderer<T> renderer;
        T block;
        MeshBundle mesh;
    }


    @SubscribeEvent
    static void ui(@NotNull UIChildEvent event) throws IOException {
        val canvas = event.canvas();
//        UICanvas top = new UICanvas();
//        top.setDimensions(160, 0, canvas.width - 160, 30);
//        top.setRoundness(0);
//        top.setColor(0xFF0A0A0A);
//        canvas.add(top);


        UIAlign align = new UIAlign();
        align.alignHorizontal();


        float size = 20;

//        UIEmpty toggle = new UIEmpty();
//        toggle.setDimensions(0,0,10,0);
//        align.add(toggle);
        UIDraggable draggable = new UIDraggable();
        draggable.setColor(ColorLib.getBackground(0.2f));
        draggable.add(align);
        draggable.disable();
        canvas.add(draggable);

        UITexture build = new UITexture();
        build.load("content/icons/block.png");
        build.setDimensions(0,0,size,size);
        build.setAction(() -> UIState.action = UIState.ActionType.PLACE);
        align.add(build);

        UITexture config = new UITexture();
        config.load("content/icons/edit.png");
        config.setDimensions(0,0,size,size);
        config.setAction(() -> UIState.action = UIState.ActionType.EDIT);
        align.add(config);

        UITexture progress = new UITexture();
        progress.load("content/icons/notebook.png");
        progress.setDimensions(0,0,size,size);
        align.add(progress);
        progress.setAction(() -> UIState.action = UIState.ActionType.INSPECT);

        align.setSpacing(4);
        align.pack();
        align.setColor(ColorLib.getBackground(0.2f));
        final var x = 160 + (canvas.width - 160f) / 2.0f - align.width / 2f;
        draggable.setDimensions(x,30,align.width+10,size);
        draggable.setGridLock(20);
        align.setDimensions(10,0,align.width,size);




//        list.clear();
//        MeshSystem.registeredMeshes().forEach((clazz, mesh) -> {
//            final BlockRenderer<?> renderer = MeshSystem.rendererByClass(clazz);
//            final Block block;
//            try {
//                block = (Block) clazz.getConstructors()[0].newInstance();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            block.setPosition(0, 0, 0);
//            list.add(new Block2DEntry(renderer, block, mesh));
//        });





//        list.forEach(block2DEntry -> {
//            Texture texture = Texture.createAttachment((int) canvas.width, (int) canvas.height);
//            texture.seal();
//            Framebuffer framebuffer = Framebuffer.generate(texture.width(), texture.height());
//            framebuffer.attach_texture(texture, GL_COLOR_ATTACHMENT0);

//            UITexture tex = new UITexture();
//            tex.texture(texture);
//            tex.setDimensions(2, 2, 26, 26);
//            align.add(tex);

//            tex.setAction(() -> {
//                try {
//                    UIState.selectedBlock = (Block) block2DEntry.block.getClass().getConstructors()[0].newInstance();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });

//            UIElement drawElement = new UIElement() {
//                @Override
//                public void drawScreen(float mouseX, float mouseY) {
//                    framebuffer.bind();
//                    framebuffer.clear(Color.TRANSPARENT);
//                    final Matrix4f projection = new Matrix4f();
//                    projection.ortho(1,
//                            -3,
//                            -1,
//                            3,
//                            -1.1f, 1000f, false);

//                    final Matrix4f view = new Matrix4f();
//                    final Vector3f position = new Vector3f(1f, -0.1f, 1f);
//                    view.lookAt(position, new Vector3f(0.5f), new Vector3f(0, 1, 0));
//                    view.scale(1.2f);

//                    final Matrix4f projView = new Matrix4f();
//                    projection.mul(view, projView);

//                    drawEntry(block2DEntry, projView);
//                    framebuffer.unbind();
//                }
//            };
//            canvas.add(drawElement);
//        });
    }

    static <T extends Block> void drawEntry(Block2DEntry<T> entry, Matrix4f mat) {
        entry.renderer.draw(entry.block, mat);
    }

}
