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

package com.mbrlabs.mundus.ui.modules.inspector.components;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
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
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetSelectionDialog;
import com.mbrlabs.mundus.ui.modules.dialogs.assets.AssetTextureFilter;
import com.mbrlabs.mundus.ui.widgets.AssetSelectionField;
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


        VisLabel label = new VisLabel("Currently you can can not change the material of a model component individually. " +
                "You can however change the default material of the underlying model asset. " +
                "To do that sleect the model in the asset browser.");
        label.setWrap(true);
        collapsibleContent.add(label).grow().row();
    }

    @Override
    public void setValues(GameObject go) {
        Component c = go.findComponentByType(Component.Type.MODEL);
        if (c != null) {
            component = (ModelComponent) c;
        }
    }

}
