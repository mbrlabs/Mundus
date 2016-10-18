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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
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

    private String label = null;

    private VisTextField textField = null;
    private VisTextButton cpBtn;
    private ColorPicker colorPicker;

    private Color color;
    private ColorSelected callback;

    public ColorPickerField(String label) {
        super();
        this.label = label;
        textField = new VisTextField();
        cpBtn = new VisTextButton("Select");
        colorPicker = new ColorPicker();
        setEditable(false);
        setupUI();
        setupListeners();
    }

    public ColorPickerField() {
        this(null);
    }

    public void setCallback(ColorSelected colorSelected) {
        this.callback = colorSelected;
    }

    public void setColor(Color color) {
        if (color != null) {
            textField.setText("#" + color.toString());
            colorPicker.setColor(color);
            this.color = color;
        }
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
        if (label != null) {
            add(new VisLabel(label)).fillX().expandX();
        }
        add(textField).padRight(5).fillX().expandX();
        add(cpBtn).row();
    }

    public void setDisabled(boolean disable) {
        cpBtn.setDisabled(disable);
        if (disable) {
            cpBtn.setTouchable(Touchable.disabled);
        } else {
            cpBtn.setTouchable(Touchable.enabled);
        }
    }

    private void setupListeners() {

        // color chooser
        colorPicker.setListener(new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                textField.setText("#" + newColor.toString());
                color = newColor;
                callback.selected(color);
            }
        });

        // color chooser button
        cpBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Ui.getInstance().addActor(colorPicker.fadeIn());
            }
        });

    }

}
