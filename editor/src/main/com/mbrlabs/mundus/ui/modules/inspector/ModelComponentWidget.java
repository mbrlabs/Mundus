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

        collapsibleContent.add(new VisLabel("Model: ")).expandX().fillX();
        collapsibleContent.add(selectBox).expandX().fillX().row();

        MModelInstance mi = component.getModelInstance();
        // iterate over nodes
        for(Node node : mi.modelInstance.nodes) {
            collapsibleContent.add(new VisLabel("Node: " + node.id)).expandX().fillX().left().row();

            // iterate over node parts
            for(NodePart nodePart : node.parts) {
                ColorAttribute diffuse = (ColorAttribute) nodePart.material.get(ColorAttribute.Diffuse);
                ColorAttribute ambient = (ColorAttribute) nodePart.material.get(ColorAttribute.Ambient);
                ColorAttribute specular = (ColorAttribute) nodePart.material.get(ColorAttribute.Specular);
                FloatAttribute shininess = (FloatAttribute) nodePart.material.get(FloatAttribute.Shininess);

                collapsibleContent.add(new VisLabel("Diffuse: " + diffuse.color.toString())).expandX().fillX().left().row();
                collapsibleContent.add(new VisLabel("Ambient: " + ambient.color.toString())).expandX().fillX().left().row();
                collapsibleContent.add(new VisLabel("Specular: " + specular.color.toString())).expandX().fillX().left().row();
                collapsibleContent.add(new VisLabel("Shininess: " + shininess.value)).expandX().fillX().left().row();

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
