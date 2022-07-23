package de.plixo.impl.block;

import de.plixo.game.Block;
import de.plixo.general.collision.EnumFacing;
import de.plixo.rendering.MeshTexture;
import de.plixo.rendering.targets.Shader;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Pipe extends Block {

    static MeshTexture middleMesh;
    static MeshTexture downMesh;
    static MeshTexture upMesh;
    static MeshTexture northMesh;
    static MeshTexture eastMesh;
    static MeshTexture southMesh;
    static MeshTexture westMesh;


    static {
        init(() -> {
            final var full = MeshTexture.generate("pipe.obj", "pipe.mtl");
            System.out.println(full.length);
            middleMesh = full[0];
            westMesh = full[1];
            eastMesh = full[5];

            northMesh = full[6];
            southMesh = full[4];

            upMesh = full[3];
            downMesh = full[2];
        });
    }

    boolean down = false;
    boolean up = false;
    boolean east = false;
    boolean north = false;
    boolean west = false;
    boolean south = false;

    @Override
    public @NotNull Shader getShader() {
        return defaultBlockShader;
    }

    @Override
    public @NotNull MeshTexture[] getMeshes() {
//        if(east) {
//            return new MeshTexture[]{eastMesh};
//        }
//        return new MeshTexture[]{upMesh,downMesh,middleMesh};
        MeshTexture[] textures = new MeshTexture[7];
        textures[0] = middleMesh;
        int index = 1;

        if (down) {
            textures[index++] = downMesh;
        }
        if (up) {
            textures[index++] = upMesh;
        }
        if (east) {
            textures[index++] = eastMesh;
        }
        if (north) {
            textures[index++] = northMesh;
        }
        if (west) {
            textures[index++] = westMesh;
        }
        if (south) {
            textures[index++] = southMesh;
        }
        return Arrays.copyOf(textures,index);

    }

    @Override
    public void onNeighborUpdate() {
        down = get(EnumFacing.DOWN) != null;
        up = get(EnumFacing.UP) != null;
        east = get(EnumFacing.EAST) != null;
        north = get(EnumFacing.NORTH) != null;
        west = get(EnumFacing.WEST) != null;
        south = get(EnumFacing.SOUTH) != null;
        super.onNeighborUpdate();
    }
}
