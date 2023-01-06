package de.plixo.intermediate;

public interface Flagged {
    boolean isDirty();
    void markDirty();
    void cleanFlag();
}
