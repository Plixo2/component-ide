package de.plixo.intermediate.statement;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public class ExpressionStatement extends Statement {
    @Getter
    @Setter
    @Accessors(fluent = true)
    @NotNull String str = "";

    @SneakyThrows
    @Override
    public @NotNull ExpressionStatement copy() {
        return (ExpressionStatement) super.clone();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionStatement that = (ExpressionStatement) o;
        return str.equals(that.str);
    }

}
