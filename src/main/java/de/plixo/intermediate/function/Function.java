package de.plixo.intermediate.function;

import de.plixo.intermediate.function.types.Type;
import de.plixo.intermediate.statement.Statement;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.linux.Stat;

import java.util.List;

@RequiredArgsConstructor
public class Function {

    public @NotNull String name;
    public @NotNull List<Argument> arguments;
    public @NotNull Type returnType;

    public @NotNull Statement statement;

}
