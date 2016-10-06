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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.assets.Asset;
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
import com.mbrlabs.mundus.ui.widgets.ColorPickerField;

import java.io.IOException;

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
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
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                ModelAsset model = selectBox.getSelected();
                if(model != null) {
                    component.getModelInstance().replaceModel(model);
                    component.encodeRaypickColorId();
                }
            }
        });

        setupUI();
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
        for(Material mat : model.materials) {
            collapsibleContent.add(new VisLabel(mat.id)).expandX().fillX().left().row();

            // diffuse texture
            collapsibleContent.add(new VisLabel("Diffuse Texture (click to change)")).expandX().fillX().left().row();
            final VisTextField diffuseTextureField = new VisTextField();
            final AssetSelectionDialog.AssetSelectionListener listener = new AssetSelectionDialog.AssetSelectionListener() {
                @Override
                public void onSelected(Array<Asset> assets) {
                    if(assets.size > 0) {
                        // set texture id & save
                        Asset selectedTexture = assets.first();
                        asset.setDiffuseTexture((TextureAsset) selectedTexture);
                        try {
                            asset.getMeta().save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // update existing models in scene graph
                        EditorScene scene = projectManager.current().currScene;
                        for(GameObject go : scene.sceneGraph.getGameObjects()) {
                            ModelComponent c = (ModelComponent) go.findComponentByType(Component.Type.MODEL);
                            if(c != null) c.getModelInstance().applyModelMaterial();
                        }

                        diffuseTextureField.setText(selectedTexture.toString());
                    }
                }
            };
            final AssetSelectionDialog.AssetFilter filter = new AssetSelectionDialog.AssetFilter() {
                @Override
                public boolean ignore(Asset asset) {
                    return !(asset instanceof TextureAsset);
                }
            };
            diffuseTextureField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Ui.getInstance().getAssetSelectionDialog().show(filter, listener);
                }
            });
            TextureAsset texAsset = component.getModelInstance().getModel().getDiffuseTexture();
            if(texAsset != null) {
                diffuseTextureField.setText(texAsset.toString());
            }
            diffuseTextureField.setDisabled(true);
            collapsibleContent.add(diffuseTextureField).expandX().fillX().left().padBottom(5).row();

            // diffuse color
            final ColorAttribute diffuse = (ColorAttribute) mat.get(ColorAttribute.Diffuse);
            ColorPickerField diffusePicker = new ColorPickerField("Diffuse: ");
            diffusePicker.setColor(diffuse.color);
            diffusePicker.setCallback(new ColorPickerField.ColorSelected() {
                @Override
                public void selected(Color color) {
                    diffuse.color.set(color);
                }
            });
            collapsibleContent.add(diffusePicker).expandX().fillX().left().padBottom(5).row();

            // ambient color
            final ColorAttribute ambient = (ColorAttribute) mat.get(ColorAttribute.Ambient);
            ColorPickerField ambientPicker = new ColorPickerField("Ambient: ");
            ambientPicker.setColor(diffuse.color);
            ambientPicker.setCallback(new ColorPickerField.ColorSelected() {
                @Override
                public void selected(Color color) {
                    ambient.color.set(color);
                }
            });
            collapsibleContent.add(ambientPicker).expandX().fillX().left().padBottom(5).row();

            // specular color
            final ColorAttribute specular = (ColorAttribute) mat.get(ColorAttribute.Specular);
            ColorPickerField specularPicker = new ColorPickerField("Specular: ");
            specularPicker.setColor(specular.color);
            specularPicker.setCallback(new ColorPickerField.ColorSelected() {
                @Override
                public void selected(Color color) {
                    specular.color.set(color);
                }
            });
            collapsibleContent.add(specularPicker).expandX().fillX().left().row();

            // shininess
            FloatAttribute shininess = (FloatAttribute) mat.get(FloatAttribute.Shininess);
        }

    }

    @Override
    public void setValues(GameObject go) {
        Component c = go.findComponentByType(Component.Type.MODEL);
        if(c != null) {
            component = (ModelComponent) c;
        }
    }

}
