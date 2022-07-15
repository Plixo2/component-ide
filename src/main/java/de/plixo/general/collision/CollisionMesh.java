package de.plixo.general.collision;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;


public class CollisionMesh {

    @Getter
    @Accessors(fluent = true)
    final @NotNull Collection<Vertex> vertices;

    @Getter
    @Accessors(fluent = true)
    @NotNull AABB boundingBox;

    public CollisionMesh(@NotNull Collection<Vertex> vertices) {
        this.vertices = vertices;
        boundingBox = new AABB(0, 0, 0, 0, 0, 0);
        generateBox();
    }

    private void generateBox() {
        if (vertices().isEmpty()) {
            this.boundingBox = new AABB(0, 0, 0, 0, 0, 0);
            return;
        }

        var x = Float.MAX_VALUE;
        var y = Float.MAX_VALUE;
        var z = Float.MAX_VALUE;
        var x2 = -Float.MAX_VALUE;
        var y2 = -Float.MAX_VALUE;
        var z2 = -Float.MAX_VALUE;
        for (Vertex vertex : vertices) {
            x = Math.min(vertex.x, x);
            y = Math.min(vertex.y, x);
            z = Math.min(vertex.z, x);
            x2 = Math.max(vertex.x, x2);
            y2 = Math.max(vertex.y, y2);
            z2 = Math.max(vertex.z, z2);
        }

        this.boundingBox = new AABB(x, y, z, x2, y2, z2);

    }

    public record Vertex(float x, float y, float z) {
    }

    @Override
    public String toString() {
        return "CollisionMesh{" +
                " boundingBox=" + boundingBox +
                '}';
    }

    public static CollisionMesh empty() {
        return new CollisionMesh(new ArrayList<>());
    }
}
