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

package com.mbrlabs.mundus.ui.components.inspector;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.ui.components.inspector.ComponentWidget;

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
public class ModelComponentWidget extends ComponentWidget<ModelComponent> {

    private VisSelectBox<MModel> selectBox = new VisSelectBox<>();

    @Inject
    private ProjectContext projectContext;

    public ModelComponentWidget(final ModelComponent modelComponent) {
        super("Model Component", modelComponent);
        Mundus.inject(this);
        this.component = modelComponent;

        selectBox.setItems(projectContext.models);
        selectBox.setSelected(modelComponent.getModelInstance().getModel());
        selectBox.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                MModel model = selectBox.getSelected();
                if(model != null) {
                    modelComponent.getModelInstance().replaceModel(model);
                }
            }
        });

        setupUI();
    }

    private void setupUI() {
        collapsibleContent.add(new VisLabel("Model: "));
        collapsibleContent.add(selectBox).expand().fill().minWidth(200).row();
    }

    @Override
    public void onDelete() {
        projectContext.currScene.currentSelection.removeComponent(component);
        remove();
    }

    @Override
    public void setValues(GameObject go) {
        Component c = go.findComponentByType(Component.Type.MODEL);
        if(c != null) {
            component = (ModelComponent) c;
        }
    }

}
