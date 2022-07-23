package de.plixo.impl.item;

import de.plixo.game.item.Item;

public class IronOre extends Item {
    public IronOre() {
        super("build.iron_ore");
        addSprite("iron_ore");
        loadDefaultItemShader();
    }

}
