package de.plixo.general.reference;

public class ExposedReference<O> extends Reference<O> {

    public O value;

    @Override
    public O getValue() {
        return value;
    }


    @Override
    public boolean hasValue() {
        return value != null;
    }

    @Override
    public void setValue(O value) {
        this.value = value;
    }

    public ExposedReference(O value) {
        this.value = value;
    }

    public ExposedReference() {
    }
}