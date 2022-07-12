package de.plixo.general.reference;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FieldReference<O> extends InterfaceReference<O> {
    String name;
    Field field;

    public FieldReference(String name , Field field, Consumer<O> setter, Supplier<O> getter) {
        super(setter, getter);
        this.name = name;
        this.field = field;
    }


    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }
}
