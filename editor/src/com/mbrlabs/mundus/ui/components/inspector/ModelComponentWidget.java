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

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.scene3d.components.Component;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
public class ModelComponentWidget extends ComponentWidget {

    private ModelComponent modelComponent;

    private VisTextField name = new VisTextField();;

    public ModelComponentWidget(Inspector inspector, ModelComponent modelComponent) {
        super(inspector, "Model Component");
        this.modelComponent = modelComponent;

        setupUI();
    }

    private void setupUI() {
        collapsibleContent.add(new VisLabel("Model: "));
        collapsibleContent.add(name).expand().fill().row();
    }

    @Override
    public void onDelete() {
        inspector.currentGO.removeComponent(modelComponent);
        remove();
    }

    @Override
    public void setValues(GameObject go) {
        Component component = go.findComponentByType(Component.Type.MODEL);
        if(component != null) {
            modelComponent = (ModelComponent) component;
            name.setText("TBD");
        }
    }

}
