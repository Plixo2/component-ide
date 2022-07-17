package de.plixo.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class Dispatcher {
    static Map<Class<?>, List<EventReceiver>> receiver = new HashMap<>();

    record EventReceiver(@Nullable Object object, @NotNull Method method) {
    }


    public static void emit(@NotNull Event event) {
        final List<EventReceiver> receivers = receiver.get(event.getClass());
        if (receivers != null) {
            receivers.forEach(ref -> {
                try {
                    ref.method.invoke(ref.object, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static void register(@NotNull Object object) {
        final Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getParameterCount() != 1 || !method.isAnnotationPresent(SubscribeEvent.class) ||
                    Modifier.isStatic(method.getModifiers()))
                continue;

            final Parameter parameter = method.getParameters()[0];
            final Class<?> input = parameter.getType();

            if (Event.class.isAssignableFrom(input)) {
                method.setAccessible(true);

                final List<EventReceiver> list =
                        receiver.computeIfAbsent(input, u -> new ArrayList<>());
                list.add(new EventReceiver(object, method));
            }
        }
    }

    public static <T> void registerStatic(@NotNull Class<T> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getParameterCount() != 1 || !method.isAnnotationPresent(SubscribeEvent.class) ||
                    !Modifier.isStatic(method.getModifiers()))
                continue;


            final Parameter parameter = method.getParameters()[0];
            final Class<?> input = parameter.getType();

            if (Event.class.isAssignableFrom(input)) {
                method.setAccessible(true);
                final List<EventReceiver> list =
                        receiver.computeIfAbsent(input, u -> new ArrayList<>());
                list.add(new EventReceiver(null, method));
            }
        }
    }
}
