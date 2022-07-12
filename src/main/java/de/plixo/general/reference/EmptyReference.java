package de.plixo.general.reference;

public class EmptyReference<O> extends Reference<O> {

    @Override
    public O getValue() {
        return null;
    }


    @Override
    public boolean hasValue() {
        return false;
    }
}
