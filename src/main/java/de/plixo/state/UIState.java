package de.plixo.state;

import de.plixo.ui.lib.elements.layout.UIAlign;
import de.plixo.ui.lib.elements.layout.UICanvas;
import org.jetbrains.annotations.NotNull;

public class UIState {
    public static boolean firstRun = true;
    public static UIAlign debugList;



    public static @NotNull ActionType action = ActionType.PLACE;


    public enum ActionType {
        PLACE,
        EDIT,
        INSPECT
    }
}
