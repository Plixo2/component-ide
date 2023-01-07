package de.plixo.general.reference;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class FieldReference<O> extends InterfaceReference<O> {
    @Getter
    @Accessors(fluent = true)
    @NotNull
    final Field field;

    public FieldReference(@NotNull Field field, final @Nullable Object object) {
        super(s -> {
            try {
                field.set(object, s);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            try {
                return (O) field.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        field.setAccessible(true);
        this.field = field;
    }

}
