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

package com.mbrlabs.mundus.ui.modules.inspector;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.MetaFile;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.model.MModelInstance;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.core.EditorScene;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetSelectionDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetTextureFilter;
import com.mbrlabs.mundus.ui.widgets.ColorPickerField;

import java.io.IOException;

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
// TODO refactor the whole class. kind of messy right now
public class ModelComponentWidget extends ComponentWidget<ModelComponent> {

    private VisSelectBox<ModelAsset> selectBox = new VisSelectBox<>();

    @Inject
    private ProjectManager projectManager;

    public ModelComponentWidget(final ModelComponent modelComponent) {
        super("Model Component", modelComponent);
        Mundus.inject(this);
        this.component = modelComponent;

        // selection box
        selectBox.setItems(projectManager.current().assetManager.getModelAssets());
        selectBox.setSelected(modelComponent.getModelInstance().getModel());
        selectBox.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                ModelAsset model = selectBox.getSelected();
                if (model != null) {
                    component.getModelInstance().replaceModel(model);
                    component.encodeRaypickColorId();
                }
            }
        });

        setupUI();
    }

    private void updateModelInstaneceMaterials() {
        EditorScene scene = projectManager.current().currScene;
        for (GameObject go : scene.sceneGraph.getGameObjects()) {
            ModelComponent c = (ModelComponent) go.findComponentByType(Component.Type.MODEL);
            if (c != null) c.getModelInstance().applyModelMaterial();
        }
    }

    private void setupUI() {
        // create Model select dropdown
        collapsibleContent.add(new VisLabel("Model")).left().row();
        collapsibleContent.addSeparator().padBottom(5).row();
        collapsibleContent.add(selectBox).expandX().fillX().row();

        // create materials for all model nodes
        MModelInstance mi = component.getModelInstance();
        collapsibleContent.add(new VisLabel("Materials")).expandX().fillX().left().padBottom(3).padTop(3).row();
        collapsibleContent.addSeparator().row();

        final ModelAsset asset = mi.getModel();
        final Model model = asset.getModel();

        // iterate over materials
        for (Material mat : model.materials) {
            collapsibleContent.add(new VisLabel(mat.id)).expandX().fillX().left().row();

            // diffuse texture
            collapsibleContent.add(new VisLabel("Diffuse Texture (click to change)")).expandX().fillX().left().row();
            final VisTextField diffuseTextureField = new VisTextField();
            final AssetSelectionDialog.AssetSelectionListener listener = assets -> {
                if (assets.size > 0) {
                    // set texture id & save
                    Asset selectedTexture = assets.first();
                    asset.setDiffuseTexture((TextureAsset) selectedTexture);
                    asset.applyDependencies();
                    try {
                        asset.getMeta().save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    updateModelInstaneceMaterials();
                    diffuseTextureField.setText(selectedTexture.toString());
                }
            };
            diffuseTextureField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Ui.getInstance().getAssetSelectionDialog().show(new AssetTextureFilter(), listener);
                }
            });
            TextureAsset texAsset = component.getModelInstance().getModel().getDiffuseTexture();
            if (texAsset != null) {
                diffuseTextureField.setText(texAsset.toString());
            }
            diffuseTextureField.setDisabled(true);
            collapsibleContent.add(diffuseTextureField).expandX().fillX().left().padBottom(5).row();

            // delete texture btn
            VisTextButton deletBtn = new VisTextButton("Remove texture");
            deletBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MetaFile meta = component.getModelInstance().getModel().getMeta();
                    meta.setDiffuseTexture(null);
                    try {
                        meta.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (Material mat : component.getModelInstance().getModel().getModel().materials) {
                        mat.remove(TextureAttribute.Diffuse);
                    }
                    updateModelInstaneceMaterials();
                    diffuseTextureField.setText("");
                }
            });
            collapsibleContent.add(deletBtn).expandX().fillX().left().padBottom(5).row();

            // diffuse color
            final ColorAttribute diffuse = (ColorAttribute) mat.get(ColorAttribute.Diffuse);
            ColorPickerField diffusePicker = new ColorPickerField("Diffuse: ");
            diffusePicker.setColor(diffuse.color);
            diffusePicker.setCallback(color -> {
                diffuse.color.set(color);
                MetaFile meta = component.getModelInstance().getModel().getMeta();
                meta.setDiffuseColor(color);
                try {
                    meta.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                updateModelInstaneceMaterials();
            });
            collapsibleContent.add(diffusePicker).expandX().fillX().left().padBottom(5).row();
        }

    }

    @Override
    public void setValues(GameObject go) {
        Component c = go.findComponentByType(Component.Type.MODEL);
        if (c != null) {
            component = (ModelComponent) c;
        }
    }

}
