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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 08-01-2016
 */
public class ColorPickerField extends VisTable {

    public static interface ColorSelected {
        public void selected(Color color);
    }

    private int width;

    private VisTextField textField;
    private VisTextButton cpBtn;
    private ColorPicker colorPicker;

    private Color color;
    private ColorSelected callback;

    public ColorPickerField(int width) {
        super();
        this.width = width;
        textField = new VisTextField();
        cpBtn = new VisTextButton("Select");
        colorPicker = new ColorPicker();

        setupUI();
        setupListeners();
    }


    public void setCallback(ColorSelected colorSelected) {
        this.callback = colorSelected;
    }

    public void setColor(Color color) {
        textField.setText("#" + color.toString());
        colorPicker.setColor(color);
        this.color = color;
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

    private void setupUI() {
        pad(5);
        add(textField).width(width*0.75f).padRight(5);
        add(cpBtn).expandX().fillX();
    }

    private void setupListeners() {

        // file chooser
        colorPicker.setListener(new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                textField.setText("#" + newColor.toString());
                color = newColor;
                callback.selected(color);
            }
        });

        // file chooser button
        cpBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Ui.getInstance().addActor(colorPicker.fadeIn());
            }
        });

    }


}
