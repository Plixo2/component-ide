package de.plixo.ui.interfaces;

public interface IKeyboard {

    boolean isKeyDown(int key);

    boolean isKeyComboCtrlX(int key);

    boolean isKeyComboCtrlV(int key);

    boolean isKeyComboCtrlC(int key);

    boolean isKeyComboCtrlA(int key);

    boolean isCtrlKeyDown();

    boolean isAltKeyDown();

    boolean isShiftKeyDown();

    String getClipboardString();

    void setClipboardString(String copyText);

    boolean isAllowedTextCharacter(char character);

    String filterAllowedCharacters(String input);

    String getKeyName(int key);


    int KEY_ESCAPE();


    int KEY_LSHIFT();

    int KEY_LCONTROL();

    int KEY_RIGHT();

    int KEY_LEFT();

    int KEY_DELETE();

    int KEY_END();

    int KEY_HOME();
    int KEY_A();
    int KEY_BACK();
    int KEY_ENTER();
}
