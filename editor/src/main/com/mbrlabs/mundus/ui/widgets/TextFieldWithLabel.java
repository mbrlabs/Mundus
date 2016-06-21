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

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public class TextFieldWithLabel extends VisTable {

    private int width = -1;

    protected VisTextField textField;
    private VisLabel label;

    public TextFieldWithLabel(String labelText, int width) {
        super();
        this.width = width;
        textField = new VisTextField();
        label = new VisLabel(labelText);
        setupUI();
    }

    public TextFieldWithLabel(String labelText) {
        this(labelText, -1);
    }

    private void setupUI() {
        if(width > 0) {
            add(label).left().width(width * 0.2f);
            add(textField).right().width(width * 0.8f).row();
        } else {
            add(label).left().expandX();
            add(textField).right().expandX().row();
        }
    }

    public String getText() {
        return textField.getText();
    }

    public void setEditable(boolean editable) {
        textField.setDisabled(!editable);
    }

    public void clear() {
        textField.setText("");
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void setLabelText(String text) {
        label.setText(toString());
    }

}
