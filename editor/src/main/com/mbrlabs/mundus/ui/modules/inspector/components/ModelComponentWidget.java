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

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.ui.widgets.MaterialWidget;

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
// TODO refactor the whole class. kind of messy right now
public class ModelComponentWidget extends ComponentWidget<ModelComponent> {

    private VisSelectBox<ModelAsset> selectBox = new VisSelectBox<>();
    private VisTable materialContainer;

    @Inject
    private ProjectManager projectManager;

    public ModelComponentWidget(final ModelComponent modelComponent) {
        super("Model Component", modelComponent);
        Mundus.inject(this);
        this.component = modelComponent;
        materialContainer = new VisTable();

        // selection box
        //        selectBox.setItems(projectManager.current().assetManager.getModelAssets());
        //        selectBox.setSelected(modelComponent.getModelAsset());
        //                selectBox.addListener(new ChangeListener() {
        //                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
        //                        ModelAsset model = selectBox.getSelected();
        //                        if (model != null) {
        //                            component.getModelInstance().replaceModel(model);
        //                            component.encodeRaypickColorId();
        //                        }
        //                    }
        //                });

        setupUI();
    }

    private void setupUI() {
        // create Model select dropdown
        collapsibleContent.add(new VisLabel("Model")).left().row();
        collapsibleContent.addSeparator().padBottom(5).row();
        //collapsibleContent.add(selectBox).expandX().fillX().row();
        collapsibleContent.add(new VisLabel("Model asset: " + component.getModelAsset().getName())).grow().padBottom(15)
                .row();

        // create materials for all model nodes
        collapsibleContent.add(new VisLabel("Materials")).expandX().fillX().left().padBottom(3).padTop(3).row();
        collapsibleContent.addSeparator().row();

        VisLabel label = new VisLabel();
        label.setWrap(true);
        label.setText("Here you change the materials of model components individually.\n"
                + "Modifing the material will update all components, that use that material.");
        collapsibleContent.add(label).grow().padBottom(10).row();

        collapsibleContent.add(materialContainer).grow().row();
        buildMaterials();
    }

    private void buildMaterials() {
        materialContainer.clear();
        for (String g3dbMatID : component.getMaterials().keySet()) {

            MaterialWidget mw = new MaterialWidget(materialAsset -> {
                component.getMaterials().put(g3dbMatID, materialAsset);
                component.applyMaterials();
            });
            mw.setMaterial(component.getMaterials().get(g3dbMatID));
            materialContainer.add(mw).grow().padBottom(20).row();
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
