package com.mbrlabs.mundus.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisRadioButton;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class RadioButtonGroup<T> extends VisTable {

    /**
     * A checkbox with a reference object.

     */
    public static class RadioButton extends VisRadioButton {

        private Object refObject;

        public RadioButton(String text, Object refObject) {
            super(text);
            this.refObject = refObject;
        }

        public Object getRefObject() {
            return refObject;
        }

    }

    private ButtonGroup<RadioButton> buttonGroup;

    public RadioButtonGroup() {
        super();
        buttonGroup = new ButtonGroup<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        pad(5);
    }

    public void add(RadioButton radioButton) {
        buttonGroup.add(radioButton);
        super.add(radioButton).left().row();
    }

    public ButtonGroup<RadioButton> getButtonGroup() {
        return buttonGroup;
    }

}
