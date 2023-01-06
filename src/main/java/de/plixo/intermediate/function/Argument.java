package de.plixo.intermediate.function;

import de.plixo.intermediate.Flagged;
import de.plixo.intermediate.function.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class Argument {
    public @NotNull String name;
    public @NotNull Type type;
}
