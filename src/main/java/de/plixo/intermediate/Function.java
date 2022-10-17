package de.plixo.intermediate;

import de.plixo.intermediate.statement.Statement;
import de.plixo.ui.lib.elements.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Function implements Cloneable, State {

    @Getter
    @Setter
    @Accessors(fluent = true)
    @NotNull String name = "";
    @NotNull String returnType = "";

    @NotNull List<Argument> arguments = new ArrayList<>();

    @Nullable Statement statement;

    public static class Argument {
        @Getter
        @Setter
        @Accessors(fluent = true)
        @NotNull String name = "";

        @Getter
        @Setter
        @Accessors(fluent = true)
        @NotNull String type = "";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return name.equals(function.name) && returnType.equals(function.returnType) &&
                arguments.equals(function.arguments) && Objects.equals(statement, function.statement);
    }

    @SneakyThrows
    @Override
    public @NotNull Function copy() {
        var clone = (Function) super.clone();
        clone.arguments = new ArrayList<>(arguments);
        return clone;
    }
}
