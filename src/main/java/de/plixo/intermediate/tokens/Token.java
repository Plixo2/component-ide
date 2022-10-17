package de.plixo.intermediate.tokens;

import de.plixo.intermediate.Icon;

public abstract class Token {
    public static int CTRL = 0x10000000;
    public static int ALT = 0x20000000;
    public static int SHIFT = 0x40000000;

    public abstract Icon icon();
    public abstract int key();
}
