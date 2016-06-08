/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
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
