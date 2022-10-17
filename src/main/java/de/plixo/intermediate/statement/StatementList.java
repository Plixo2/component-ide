package de.plixo.intermediate.statement;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StatementList extends Statement {
    public @NotNull List<Statement> list = new ArrayList<>();

    @SneakyThrows
    @Override
    public @NotNull StatementList copy() {
        var clone = (StatementList) super.clone();
        clone.list = new ArrayList<>(list);
        return clone;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementList that = (StatementList) o;
        return list.equals(that.list);
    }

}
