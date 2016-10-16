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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.assets.EditorAssetManager;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetTextureFilter;
import com.mbrlabs.mundus.utils.Log;

import java.io.IOException;

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
public class MaterialWidget extends VisTable {

    private static final String TAG = MaterialWidget.class.getSimpleName();

    private VisLabel label;
    private ColorPickerField diffuseColorField;
    private AssetSelectionField diffuseAssetField;
    private VisTextField opacity;
    private VisTextField shininess;

    private VisTextButton saveBtn;

    private MaterialAsset material;

    @Inject
    private ProjectManager projectManager;

    public MaterialWidget() {
        super();
        Mundus.inject(this);
        align(Align.topLeft);
        label = new VisLabel();
        diffuseAssetField = new AssetSelectionField();
        diffuseColorField = new ColorPickerField();
        opacity = new VisTextField();
        shininess = new VisTextField();
        saveBtn = new VisTextButton("Save material");

        shininess.setTextFieldFilter(new FloatDigitsOnlyFilter(false));
        opacity.setTextFieldFilter(new FloatDigitsOnlyFilter(false));

        label.setWrap(true);

        add(label).grow().row();
        addSeparator().growX().row();
        add(new VisLabel("Diffuse texture")).grow().row();
        add(diffuseAssetField).growX().row();
        add(new VisLabel("Diffuse color")).grow().row();
        add(diffuseColorField).growX().row();
        add(new VisLabel("Shininess")).growX().row();
        add(shininess).growX().row();
        add(new VisLabel("Opacity")).growX().row();
        add(opacity).growX().row();

        add(saveBtn).growX().padTop(5).row();

        setupWidgets();
    }

    private void setupWidgets() {
        // diffuse texture
        diffuseAssetField.setFilter(new AssetTextureFilter());
        diffuseAssetField.setListener(asset -> material.setDiffuseTexture((TextureAsset) asset));

        // diffuse color
        diffuseColorField.setCallback(color -> material.setDiffuseColor(color));

        opacity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    material.setOpacity(Float.valueOf(opacity.getText()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        shininess.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    material.setShininess(Float.valueOf(shininess.getText()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        // save material
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    EditorAssetManager assetManager = projectManager.current().assetManager;
                    assetManager.saveMaterialAsset(material);
                    Ui.getInstance().getToaster().success("Material saved");
                } catch (IOException e) {
                    Log.exception(TAG, e);
                    Ui.getInstance().getToaster().error("Error while saving the material");
                }
            }
        });
    }

    public void setMaterial(MaterialAsset material) {
        this.material = material;
        diffuseColorField.setColor(material.getDiffuseColor());
        diffuseAssetField.setAsset(material.getDiffuseTexture());
        opacity.setText(String.valueOf(material.getOpacity()));
        shininess.setText(String.valueOf(material.getShininess()));
        label.setText(material.getName());
    }

    public MaterialAsset getMaterial() {
        return material;
    }

}
