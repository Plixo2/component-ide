package de.plixo.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class ArrayStorage3D<T> implements Storage3D<T> {
    int size;
    Object[][][] storage;

    public ArrayStorage3D(T[][][] storage, int size) {
        this.size = size;
        this.storage = storage;
    }

    public ArrayStorage3D(int size) {
        this.size = size;
        this.storage = new Object[size][size][size];
    }

    public @Nullable T get(int x, int y, int z) {
        if (x < 0 || x >= size || y < 0 || y >= size || z < 0 || z >= size) {
            //TODO throw?
            return null;
        }
        return (T) storage[x][y][z];
    }

    public void insert(@Nullable T obj,int x, int y, int z) {
        storage[x][y][z] = obj;
    }


}
