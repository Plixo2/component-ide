package de.plixo.game.blocks;


import de.plixo.game.item.Item;

public class ItemFilter {
    private boolean isBlacklist = false;

    private boolean ignoreMetaData = false;


    private boolean isBlacklist() {
        return isBlacklist;
    }

    private boolean isWhitelist() {
        return !isBlacklist;
    }

    private Item[] items;


}
