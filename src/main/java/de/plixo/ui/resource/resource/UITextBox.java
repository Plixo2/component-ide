package com.plixo.ui.resource.resource;

import com.plixo.ui.ColorLib;
import com.plixo.ui.resource.UIReferenceHolderCanvas;
import com.plixo.util.Vector2f;

public class UITextBox extends UIReferenceHolderCanvas<String> {

    boolean scissor = false;
    public UITextBox() {
        super();
        setColor(ColorLib.getBackground(0.3f));
    }
    public com.plixo.ui.resource.resource.text.TextBox field;

    @Override
    public void drawScreen(float mouseX, float mouseY) {

        drawDefault();
        drawHoverEffect();

        gui.pushMatrix();
        float[] mat = gui.getModelViewMatrix();
        Vector2f globalPosMIN = gui.toScreenSpace(mat,x,y);
        Vector2f globalPosMAX = gui.toScreenSpace(mat,x + width,y + height);
        if(scissor) {
            gui.createScissorBox(globalPosMIN.x, globalPosMIN.y, globalPosMAX.x, globalPosMAX.y);
            gui.activateScissor();
        }
        gui.translate(x, y);
        field.drawTextBox();
        if(scissor) {
            gui.deactivateScissor();
        }
        gui.popMatrix();


        super.base(mouseX, mouseY);
    }



    //inputs
    @Override
    public void keyTyped(char typedChar, int keyCode) {
        field.setText(reference.getValue());
        field.textboxKeyTyped(typedChar, keyCode);
        reference.setValue(field.getText());
        super.keyTyped(typedChar, keyCode);
    }


    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        field.mouseClicked((int) (mouseX-x), (int)(mouseY-y), mouseButton);
        field.setFocused(isHovered(mouseX,mouseY));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    //set text field dimensions
    @Override
    public void setDimensions(float x, float y, float width, float height) {
        field = new com.plixo.ui.resource.resource.text.TextBox(0, gui,keyboard, 4, 0,
                width - 8, height);

        field.setTextColor(ColorLib.getTextColor());
        field.setMaxStringLength(10000);
        field.setText(reference.getValue());
        super.setDimensions(x, y, width, height);
    }
    public void setScissor(boolean scissor) {
        this.scissor = scissor;
    }
}
