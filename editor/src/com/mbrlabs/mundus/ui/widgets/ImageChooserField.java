/*
 * Copyright (c) 2016. See AUTHORS file.
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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.FileFormatUtils;

/**
 * Created by marcus on 1/10/16.
 */
public class ImageChooserField extends VisTable {

    private int width;

    private VisTextButton fcBtn;
    private FileChooser fileChooser;

    private Image img;
    private Texture texture;
    private FileHandle fileHandle;

    public ImageChooserField(int width) {
        super();
        this.width = width;
        fcBtn = new VisTextButton("Select");
        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        img = new Image();

        setupUI();
        setupListeners();
    }


    public FileHandle getFile() {
        return this.fileHandle;
    }

    public void setButtonText(String text) {
        fcBtn.setText(text);
    }

    public void setImage(FileHandle fileHandle) {
        if(texture != null) {
            texture.dispose();
        }

        this.fileHandle = fileHandle;

        if(fileHandle != null) {
            texture = new Texture(fileHandle);
            img.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        } else {
            img.setDrawable(null);
        }
    }

    private void setupUI() {
        pad(5);
        add(img).width(width).height(width).expandX().fillX().row();
        add(fcBtn).width(width).padTop(5).expandX();
    }

    private void setupListeners() {

        // file chooser
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(FileHandle file) {
                if(FileFormatUtils.isImage(file)) {
                    setImage(file);
                } else {
                    DialogUtils.showErrorDialog(Ui.getInstance(), "This is no image");
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
