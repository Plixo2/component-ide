package de.plixo.ui.elements.layout;


import de.plixo.ui.elements.UIElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class UICollection<A> extends UIAlign {

    List<? extends A> copy = new ArrayList<>();
    List<? extends A> list = new ArrayList<>();
    Function<A, UIElement> elementSupplier;

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        if (checkChange()) {
            copy.clear();
            copy = new ArrayList<>(list);
            constructElements();
        }
        super.drawScreen(mouseX, mouseY);
    }

    void constructElements() {
        if (elementSupplier == null && list != null) {
            return;
        }
        clear();
        list.forEach(a -> {
            UIElement element = elementSupplier.apply(a);
            add(element);
        });
    }

    boolean checkChange() {
        if (list.size() != copy.size()) {
            return true;
        }
        for (int i = 0; i < list.size(); i++) {
            A obj = list.get(i);
            A obj2 = copy.get(i);
            if (obj != obj2) {
                return true;
            }
        }
        return false;
    }

    public Collection<? extends A> getOriginal() {
        return list;
    }

    public void setList(List<? extends A> list) {
        this.list = list;
        constructElements();
    }

    public void setSupplier(Function<A, UIElement> supplier) {
        this.elementSupplier = supplier;
        constructElements();
    }

}
