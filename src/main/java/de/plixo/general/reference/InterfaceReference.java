package de.plixo.general.reference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InterfaceReference<O> extends Reference<O> {

    Consumer<O> setter;
    Supplier<O> getter;

    @Override
    public O getValue() {
        return getter.get();
    }

    @Override
    public void setValue(O value) {
        setter.accept(value);
        super.setValue(value);
    }

    @Override
    public boolean hasValue() {
        return getter != null && getter.get() != null;
    }

    public InterfaceReference(Consumer<O> setter, Supplier<O> getter) {
        this.setter = setter;
        this.getter = getter;
    }

}
