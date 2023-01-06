package de.plixo.intermediate.function.types;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ArrayType extends Type {
    public @NotNull Type elementType;
}
