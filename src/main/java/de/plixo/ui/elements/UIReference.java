package de.plixo.ui.elements;


import de.plixo.general.reference.Reference;
import de.plixo.ui.elements.layout.UICanvas;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class UIReference<O> extends UICanvas {
    protected Reference<O> reference = null;
}
