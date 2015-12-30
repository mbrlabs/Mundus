/*
 * Copyright (c) 2015. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.ui.widgets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 30-12-2015
 */
public class FileChooserField extends VisTable {

    public static interface FileSelected {
        public void selected(FileHandle fileHandle);
    }

    private FileSelected fileSelected;

    private VisTextField textField;
    private VisTextButton fcBtn;
    private FileChooser fileChooser;

    private int width;

    public FileChooserField(int width) {
        super();
        this.width = width;
        textField = new VisTextField();
        fcBtn = new VisTextButton("Select");
        fileChooser = new FileChooser(FileChooser.Mode.OPEN);

        setupUI();
        setupListeners();
    }

    public void setEditable(boolean editable) {
        textField.setDisabled(editable);
    }

    public String getPath() {
        return textField.getText();
    }

    public void setCallback(FileSelected fileSelected) {
        this.fileSelected = fileSelected;
    }

    private void setupUI() {
        pad(5);
        add(textField).width(width*0.75f).padRight(5);
        add(fcBtn).expandX().fillX();
    }

    private void setupListeners() {

        // file chooser
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(FileHandle file) {
                super.selected(file);
                textField.setText(file.path());
                if(fileSelected != null) {
                    fileSelected.selected(file);
                }
            }
        });

        // file chooser button
        fcBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Ui.getInstance().addActor(fileChooser.fadeIn());
            }
        });

    }


}
