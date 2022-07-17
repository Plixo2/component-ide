package de.plixo.game;

import de.plixo.game.collision.AABB;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

@Getter
@Accessors(fluent = true)
public class Block {

    private int x = -1;
    private int y = -1;
    private int z = -1;
    @NotNull AABB aabb = new AABB(0, 0, 0, 0, 0, 0);

    public void setPosition(int x, int y, int z) {
        if (this.x != -1 || this.y != -1 || this.z != -1) {
            throw new RuntimeException("Block position was already set");
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.aabb = new AABB(x, y, z, x + 1, y + 1, z + 1);
    }

    public Vector3i veci() {
        return new Vector3i(x, y, z);
    }


}
