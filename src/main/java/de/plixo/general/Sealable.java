package de.plixo.general;

import lombok.Getter;
import lombok.experimental.Accessors;

public class Sealable {
    @Getter
    @Accessors(fluent = true)
    private boolean sealed;

    public void seal() {
        assertNotSealed();
        this.sealed = true;
    }

    public void assertSealed() {
        assert this.sealed : this + " is not sealed";
    }

    public void assertNotSealed() {
        assert !this.sealed : this + " is already sealed";
    }

    @Deprecated
    public void modify(Runnable runnable) {
        assertSealed();
        sealed = false;
        runnable.run();
        //to detect boxing
        assertNotSealed();
        this.sealed = true;
    }
}
