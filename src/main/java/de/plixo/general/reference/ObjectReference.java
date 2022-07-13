package de.plixo.general.reference;


public class ObjectReference<O> extends Reference<O> {

    private O object;

    @Override
    public O getValue() {
        return object;
    }

    @Override
    public void setValue(O value) {
        this.object = value;
        super.setValue(value);
    }

    @Override
    public boolean hasValue() {
        return object != null;
    }

    public ObjectReference() {

    }

    public ObjectReference(O object) {
        this.object = object;
    }
}
