package de.plixo.event;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AssetServer {
    private static final Map<Class<?>, Object> storage = new HashMap<>();

    public static <T> @NotNull T get(@NotNull Class<T> cls) {
        val o = storage.get(cls);
        if (o == null) {
            throw new RuntimeException("Cant find Asset for type " + cls);
        }
        return (T) o;
    }

    public static void insert(@NotNull Object obj) {
        if (storage.containsKey(obj.getClass())) {
            throw new RuntimeException("Asset is already in the server " + obj);
        }
        storage.put(obj.getClass(), obj);
    }

    public static <T> void insert(@NotNull Class<T> cls, @NotNull T obj) {
        if (storage.containsKey(cls)) {
            throw new RuntimeException("Asset is already in the server " + obj);
        }
        storage.put(cls, obj);
    }

    public static void insertAndRegister(@NotNull Object obj) {
        if (storage.containsKey(obj.getClass())) {
            throw new RuntimeException("Asset is already in the server " + obj);
        }
        storage.put(obj.getClass(), obj);
        Dispatcher.register(obj);
    }

    public static <T> void insertAndRegister(@NotNull Class<T> cls, @NotNull T obj) {
        if (storage.containsKey(cls)) {
            throw new RuntimeException("Asset is already in the server " + obj);
        }
        storage.put(cls, obj);
        Dispatcher.register(obj);
    }

    public static void update(@NotNull Object obj) {
        storage.put(obj.getClass(), obj);
    }

    public static <T> void update(@NotNull Class<T> cls, @NotNull T obj) {
        storage.put(cls, obj);
    }
}
