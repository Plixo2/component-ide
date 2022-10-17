package de.plixo.state;

import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public class UIState {


    @Getter
    @Setter
    @Accessors(fluent = true)
    static float delta_time;

    public static boolean firstRun = true;
    public static UIAlign debugList;

}
