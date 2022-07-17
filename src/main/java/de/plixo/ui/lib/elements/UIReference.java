package de.plixo.ui.lib.elements;


import de.plixo.general.reference.Reference;
import de.plixo.ui.lib.elements.layout.UICanvas;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class UIReference<O> extends UICanvas {
    protected Reference<O> reference = null;
}
