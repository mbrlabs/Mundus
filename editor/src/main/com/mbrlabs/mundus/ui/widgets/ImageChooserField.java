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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.FileFormatUtils;

/**
 * @author Marcus Brummer
 * @version 10-01-2016
 */
public class ImageChooserField extends VisTable {

    private static final Drawable PLACEHOLDER_IMG = new TextureRegionDrawable(
            new TextureRegion(new Texture(Gdx.files.internal("ui/img_placeholder.png"))));

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
        img = new Image(PLACEHOLDER_IMG);

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
            img.setDrawable(PLACEHOLDER_IMG);
        }
    }

    private void setupUI() {
        pad(5);
        add(img).width(width).height(width).expandX().fillX().row();
        add(fcBtn).width(width).padTop(5).expandX();
    }

    private void setupListeners() {

        // file chooser
        fileChooser.setListener(new SingleFileChooserListener() {
            public void selected(FileHandle file) {
                if(FileFormatUtils.isImage(file)) {
                    setImage(file);
                } else {
                    Dialogs.showErrorDialog(Ui.getInstance(), "This is no image");
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
