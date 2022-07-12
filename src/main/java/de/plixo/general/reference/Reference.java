package de.plixo.general.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class Reference<O> {
    private final List<Consumer<O>> observers = new ArrayList<>();
    public abstract O getValue();

    public void setValue(O value) {
        observers.forEach(ref -> ref.accept(value));
    }

    public abstract boolean hasValue();

    public void addObserver(Consumer<O> current) {
        observers.add(current);
    }
}
