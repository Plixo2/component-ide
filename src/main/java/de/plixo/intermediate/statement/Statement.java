package de.plixo.intermediate.statement;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Statement {
    public @NotNull List<Token> foundTokens = new ArrayList<>();
    public @NotNull String currentText = "";

    public void endToken() {
        var cut = currentText.trim().toLowerCase();
        this.currentText = "";

        Token foundToken = new Token("?", cut, -1);
        if (cut.equals("if")) {
            foundToken = new Token("if", "if", 0xFFFF2424);
        } else if (cut.equals("(")) {
            foundToken = new Token("(", "(", 0xFF2C6CBF);
        } else if (cut.equals(")")) {
            foundToken = new Token(")", ")", 0xFF2C6CBF);
        } else if (cut.equals("==")) {
            foundToken = new Token("==", "==", 0xFFFF2424);
        } else if (cut.matches("(\\d|\\.)+")) {
            foundToken = new Token("number", cut,0xFF25BA73);
        }
        foundTokens.add(foundToken);
    }

    @AllArgsConstructor
    public static class Token {
        public @NotNull String name;
        public @NotNull String value;
        public int color;
    }
}
