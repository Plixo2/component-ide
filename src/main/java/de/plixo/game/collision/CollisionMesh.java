package de.plixo.game.collision;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class CollisionMesh {

    @Getter
    @Accessors(fluent = true)
    final @NotNull List<Triangle> triangles;

    @Getter
    @Accessors(fluent = true)
    @NotNull AABB boundingBox;

    public CollisionMesh(@NotNull List<Vertex> vertices, @NotNull List<Integer> indices) {
        assert indices.size() % 3 == 0;
        triangles = new ArrayList<>();
        for (int i = 0; i < indices.size(); i += 3) {
            final var indexA = indices.get(i);
            final var indexB = indices.get(i + 1);
            final var indexC = indices.get(i + 2);
            final var a = vertices.get(indexA);
            final var b = vertices.get(indexB);
            final var c = vertices.get(indexC);
            triangles.add(new Triangle(a, b, c));
        }

        boundingBox = new AABB(0, 0, 0, 0, 0, 0);
        generateBox();
    }

    private void generateBox() {
        if (triangles.isEmpty()) {
            this.boundingBox = new AABB(0, 0, 0, 0, 0, 0);
            return;
        }

        var x = 100000f;
        var y = 100000f;
        var z = 100000f;
        var x2 = -100000f;
        var y2 = -100000f;
        var z2 = -100000f;
        for (Triangle triangle : triangles) {
            for (Vertex vertex : triangle.list()) {
                x = Math.min(vertex.x,x);
                y = Math.min(vertex.y,y);
                z = Math.min(vertex.z,z);
                x2 = Math.max(vertex.x,x2);
                y2 = Math.max(vertex.y,y2);
                z2 = Math.max(vertex.z,z2);
            }
        }
        this.boundingBox = new AABB(x, y, z, x2, y2, z2);
    }

    public record Vertex(float x, float y, float z) {
    }

    public record Triangle(@NotNull Vertex a, @NotNull Vertex b, @NotNull Vertex c) {
        public @NotNull Vertex[] list() {
            return new Vertex[]{a, b, c};
        }
    }

    @Override
    public String toString() {
        return "CollisionMesh{" +
                " boundingBox=" + boundingBox +
                '}';
    }

    public static CollisionMesh empty() {
        return new CollisionMesh(new ArrayList<>(), new ArrayList<>());
    }
}
