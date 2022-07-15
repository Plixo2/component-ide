package de.plixo.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.Iterator;

public interface Storage3D<T> {

    default @Nullable T get(@NotNull Vector3i position) {
        return get(position.x, position.y, position.z);
    }

    @Nullable T get(int x, int y, int z);

    default void insert(@Nullable T obj, @NotNull Vector3i position) {
        insert(obj, position.x, position.y, position.z);
    }

    void insert(@Nullable T obj, int x, int y, int z);

    int size();

}
