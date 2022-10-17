package de.plixo.intermediate.statement;

import de.plixo.intermediate.tokens.Token;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TokenStatement extends Statement{
    List<Class<? extends Token>> tokens = new ArrayList<>();
    @SneakyThrows
    @Override
    public @NotNull TokenStatement copy() {
        var clone = (TokenStatement) super.clone();
        clone.tokens = new ArrayList<>(tokens);
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenStatement that = (TokenStatement) o;
        return Objects.equals(tokens, that.tokens);
    }

}
