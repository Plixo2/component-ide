package de.plixo.general.reference;

import org.plixo.gsonplus.ExposeField;

public class Resource<O> extends Reference<O> {

    String name;
    @ExposeField
    O value;
    @ExposeField
    Class<O> defaultClass;

    public Resource(String name, O value) {
        assert value != null;
        this.name = name;
        this.value = value;
        defaultClass = (Class<O>) value.getClass();
    }

    public void setValue(O value) {
        this.value = value;
        super.setValue(value);
    }

    @Override
    public boolean hasValue() {
        return getValue() != null;
    }

    public O getValue() {
        return value;
    }


    public String getName() {
        return name;
    }

    public Class<O> getDefaultClass() {
        return defaultClass;
    }


    @Override
    public String toString() {
        return "Resource{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", defaultClass=" + (defaultClass == null ? "null" : defaultClass.getSimpleName()) +
                '}';
    }
}
