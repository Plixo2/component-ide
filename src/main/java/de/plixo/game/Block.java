package de.plixo.game;

import de.plixo.general.collision.AABB;
import de.plixo.general.collision.CollisionMesh;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public class Block {


    @Getter
    @Accessors(fluent = true)
    private int x = -1;

    @Getter
    @Accessors(fluent = true)
    private int y = -1;

    @Getter
    @Accessors(fluent = true)
    private int z = -1;


    @Getter
    @Accessors(fluent = true)
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

}
