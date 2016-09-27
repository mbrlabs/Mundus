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
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MModelInstance;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.ui.widgets.ColorPickerField;

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
public class ModelComponentWidget extends ComponentWidget<ModelComponent> {

    private VisSelectBox<MModel> selectBox = new VisSelectBox<>();

    @Inject
    private ProjectManager projectManager;

    public ModelComponentWidget(final ModelComponent modelComponent) {
        super("Model Component", modelComponent);
        Mundus.inject(this);
        this.component = modelComponent;

        selectBox.setItems(projectManager.current().models);
        selectBox.setSelected(modelComponent.getModelInstance().getModel());
        selectBox.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                MModel model = selectBox.getSelected();
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
        // iterate over nodes
        for(Node node : mi.modelInstance.nodes) {
            collapsibleContent.add(new VisLabel("Node: " + node.id)).expandX().fillX().left().row();

            // iterate over node parts
            for(NodePart nodePart : node.parts) {

                // diffuse
                final ColorAttribute diffuse = (ColorAttribute) nodePart.material.get(ColorAttribute.Diffuse);
                ColorPickerField diffusePicker = new ColorPickerField("Diffuse: ");
                diffusePicker.setColor(diffuse.color);
                diffusePicker.setCallback(new ColorPickerField.ColorSelected() {
                    @Override
                    public void selected(Color color) {
                        diffuse.color.set(color);
                    }
                });
                collapsibleContent.add(diffusePicker).expandX().fillX().left().padBottom(5).row();

                // ambient
                final ColorAttribute ambient = (ColorAttribute) nodePart.material.get(ColorAttribute.Ambient);
                ColorPickerField ambientPicker = new ColorPickerField("Ambient: ");
                ambientPicker.setColor(diffuse.color);
                ambientPicker.setCallback(new ColorPickerField.ColorSelected() {
                    @Override
                    public void selected(Color color) {
                        ambient.color.set(color);
                    }
                });
                collapsibleContent.add(ambientPicker).expandX().fillX().left().padBottom(5).row();

                // specular
                final ColorAttribute specular = (ColorAttribute) nodePart.material.get(ColorAttribute.Specular);
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
                FloatAttribute shininess = (FloatAttribute) nodePart.material.get(FloatAttribute.Shininess);
            }
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
