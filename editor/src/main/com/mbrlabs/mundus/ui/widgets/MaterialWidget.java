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
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetMaterialFilter;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetSelectionDialog;
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
    private AssetSelectionField assetSelectionField;
    private ColorPickerField diffuseColorField;
    private AssetSelectionField diffuseAssetField;
    private VisTextField opacity;
    private VisTextField shininess;

    private MaterialAsset material;

    private MaterialChangedListener changedListener;

    @Inject
    private ProjectManager projectManager;

    public MaterialWidget(MaterialChangedListener listener) {
        super();
        Mundus.inject(this);
        align(Align.topLeft);
        label = new VisLabel();
        this.changedListener = listener;
        if(listener != null) {
            assetSelectionField = new AssetSelectionField();
            assetSelectionField.setFilter(new AssetMaterialFilter());
        }
        diffuseAssetField = new AssetSelectionField();
        diffuseColorField = new ColorPickerField();
        opacity = new VisTextField();
        shininess = new VisTextField();

        shininess.setTextFieldFilter(new FloatDigitsOnlyFilter(false));
        opacity.setTextFieldFilter(new FloatDigitsOnlyFilter(false));

        label.setWrap(true);

        add(label).grow().row();
        addSeparator().growX().row();
        if(changedListener != null) {
            add(new VisLabel("Change material")).growX().row();
            add(assetSelectionField).growX().padBottom(10).row();
        }
        add(new VisLabel("Diffuse texture")).grow().row();
        add(diffuseAssetField).growX().row();
        add(new VisLabel("Diffuse color")).grow().row();
        add(diffuseColorField).growX().row();
//        add(new VisLabel("Shininess")).growX().row();
//        add(shininess).growX().row();
//        add(new VisLabel("Opacity")).growX().row();
//        add(opacity).growX().row();

        setupWidgets();
    }

    private void setupWidgets() {
        // material changing
        if(changedListener != null) {
            assetSelectionField.setListener(asset -> {
                changedListener.materialChanged((MaterialAsset) asset);
                setMaterial((MaterialAsset) asset);
            });
        }

        // diffuse texture
        diffuseAssetField.setFilter(new AssetTextureFilter());
        diffuseAssetField.setListener(asset -> {
            material.setDiffuseTexture((TextureAsset) asset);
            applyMaterialToModelComponents();
            projectManager.current().assetManager.addDirtyAsset(material);
        });

        // diffuse color
        diffuseColorField.setCallback(color -> {
            material.setDiffuseColor(color);
            applyMaterialToModelComponents();
            projectManager.current().assetManager.addDirtyAsset(material);
        });

        // TODO apply materials to model components
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

        // TODO apply materials to model components
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
    }

    // TODO find better solution than iterating through all components
    private void applyMaterialToModelComponents() {
        SceneGraph sceneGraph = projectManager.current().currScene.sceneGraph;
        for(GameObject go : sceneGraph.getGameObjects()) {
            ModelComponent mc = (ModelComponent) go.findComponentByType(Component.Type.MODEL);
            if(mc != null) {
                mc.applyMaterials();
            }
        }
    }

    public void setMaterial(MaterialAsset material) {
        this.material = material;
        diffuseColorField.setColor(material.getDiffuseColor());
        if(assetSelectionField != null) {

            assetSelectionField.setAsset(material);
        }
        diffuseAssetField.setAsset(material.getDiffuseTexture());
        opacity.setText(String.valueOf(material.getOpacity()));
        shininess.setText(String.valueOf(material.getShininess()));
        label.setText(material.getName());
    }

    public MaterialAsset getMaterial() {
        return material;
    }

    /**
     *
     */
    public static interface MaterialChangedListener {
        public void materialChanged(MaterialAsset materialAsset);
    }

}
