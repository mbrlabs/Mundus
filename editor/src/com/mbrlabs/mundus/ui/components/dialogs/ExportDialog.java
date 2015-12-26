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

package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class ExportDialog extends BaseDialog {

    private static final String TAG = ExportDialog.class.getSimpleName();

    // UI elements
    private FileChooser fileChooser;
    private VisTextField output = new VisTextField();
    private VisTextButton exportBtn = new VisTextButton("EXPORT");
    private VisTextButton fileChooserBtn = new VisTextButton("Select");

    private VisCheckBox gzipCheckbox = new VisCheckBox("Compress");
    private VisCheckBox prettyPrintCheckbox = new VisCheckBox("Pretty print");

    public ExportDialog() {
        super("Export");
        //Mundus.inject(this);
        setModal(true);
        setMovable(false);
        setupUI();
        setupListener();
    }

    private void setupUI() {
        gzipCheckbox.left();
        prettyPrintCheckbox.left();
        Table root = new Table();
        // root.debugAll();
        root.padTop(6).padRight(6).padBottom(22);
        add(root).left().top();
        root.add(output).width(320).padRight(7).padBottom(5).left();
        root.add(fileChooserBtn).width(80).left().padBottom(5).row();
        root.add(gzipCheckbox).width(400).colspan(2).row();
        root.add(prettyPrintCheckbox).width(400).colspan(2).row();
        root.add(exportBtn).width(400).padTop(15).colspan(2).row();

        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.DIRECTORIES);
    }

    private void setupListener() {
        // import btn
        exportBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });

        // button launches file chooser
        fileChooserBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Ui.getInstance().addActor(fileChooser.fadeIn());
            }
        });

        // file chooser
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(FileHandle file) {
                super.selected(file);
                output.setText(file.path());
            }
        });

    }

    @Override
    protected void close() {
        super.close();
    }

}
