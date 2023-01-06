package de.plixo.ui.impl;


import de.plixo.general.IO;
import de.plixo.ui.lib.interfaces.IKeyboard;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class GLFWKeyboard implements IKeyboard {

    @Override
    public boolean isKeyDown(int i) {
        return IO.keyDown(i);
    }

    @Override
    public boolean isKeyComboCtrlX(int i) {
        return i == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    @Override
    public boolean isKeyComboCtrlV(int i) {
        return i == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    @Override
    public boolean isKeyComboCtrlC(int i) {
        return i == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    @Override
    public boolean isKeyComboCtrlA(int i) {
        return i == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    @Override
    public boolean isCtrlKeyDown() {
        return IO.keyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || IO.keyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    @Override
    public boolean isAltKeyDown() {
        return IO.keyDown(GLFW.GLFW_KEY_LEFT_ALT) || IO.keyDown(GLFW.GLFW_KEY_RIGHT_ALT);
    }

    @Override
    public boolean isShiftKeyDown() {
        return IO.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || IO.keyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Override
    public String getClipboardString() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object) null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception var1) {
            ;
        }

        return "";
    }

    @Override
    public void setClipboardString(String copyText) {
        if (copyText != null && copyText.length() > 0) {
            try {
                StringSelection stringselection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, (ClipboardOwner) null);
            } catch (Exception var2) {
                ;
            }
        }
    }


    @Override
    public boolean isAllowedTextCharacter(char character) {
        final boolean b = character != 167 && character >= 32 && character != 127;
        return b;
    }

    @Override
    public String filterAllowedCharacters(String input) {

        StringBuilder stringbuilder = new StringBuilder();

        for (char c0 : input.toCharArray()) {
            if (isAllowedTextCharacter(c0)) {
                stringbuilder.append(c0);
            }
        }
        return stringbuilder.toString();
    }

    @Override
    public String getKeyName(int i) {
        return GLFW.glfwGetKeyName(i, 0);
    }

    @Override
    public int KEY_ESCAPE() {
        return GLFW.GLFW_KEY_ESCAPE;
    }


    @Override
    public int KEY_LSHIFT() {
        return GLFW.GLFW_KEY_LEFT_SHIFT;
    }

    @Override
    public int KEY_LCONTROL() {
        return GLFW.GLFW_KEY_LEFT_CONTROL;
    }

    @Override
    public int KEY_RIGHT() {
        return GLFW.GLFW_KEY_RIGHT;
    }

    @Override
    public int KEY_LEFT() {
        return GLFW.GLFW_KEY_LEFT;
    }

    @Override
    public int KEY_DELETE() {
        return GLFW.GLFW_KEY_DELETE;
    }

    @Override
    public int KEY_END() {
        return GLFW.GLFW_KEY_END;
    }

    @Override
    public int KEY_HOME() {
        return GLFW.GLFW_KEY_HOME;
    }

    @Override
    public int KEY_A() {
        return GLFW.GLFW_KEY_A;
    }

    @Override
    public int KEY_BACK() {
        return GLFW.GLFW_KEY_BACKSPACE;
    }

    @Override
    public int KEY_ENTER() {
        return GLFW.GLFW_KEY_ENTER;
    }
}
