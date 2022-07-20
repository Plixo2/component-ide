package de.plixo.game.meta;

import de.plixo.general.Color;
import org.joml.Vector3f;
import org.plixo.gsonplus.ExposeField;

import java.util.ArrayList;
import java.util.List;

public class MetaTest {
    public List<Item> items = new ArrayList<>();

    public MetaTest() {
//        items.add(new Vector3f(0,-10,1));
        items.add(new AbstractItem(13, "hello"));
        items.add(new AbstractItem(-1, "asgd"));
        items.add(new AbstractItem2(-1, new Color(0xFF00FF00)));
    }

    public static class AbstractItem extends Item {
        @ExposeField
        String name;

        public AbstractItem(int amount, String name) {
            super(amount);
            this.name = name;
        }

        @Override
        public String toString() {
            return "AbstractItem{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static class AbstractItem2 extends Item {
        @ExposeField
        Color color;

        @ExposeField
        Vector3f position;

        public AbstractItem2(int amount, Color color) {
            super(amount);
            this.color = color;
            position = new Vector3f(0, -1, 100);
        }

        @Override
        public String toString() {
            return "AbstractItem2{" +
                    "color=" + color +
                    ", position=" + position +
                    '}';
        }
    }

    public static class Item {
        @ExposeField
        int amount;

        public Item(int amount) {
            this.amount = amount;
        }

        public Item() {
        }

        @Override
        public String toString() {
            return "Item{" +
                    "amount=" + amount +
                    '}';
        }
    }
}
