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

package com.mbrlabs.mundus.ui.modules.inspector.assets;

import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.ui.modules.inspector.BaseInspectorWidget;
import com.mbrlabs.mundus.ui.widgets.MaterialWidget;

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
public class MaterialInspectorWidget extends BaseInspectorWidget {

    private static final String TITLE = "Material Asset";

    private MaterialAsset material;

    private MaterialWidget materialWidget;

    public MaterialInspectorWidget() {
        super(TITLE);
        setDeletable(false);

        materialWidget = new MaterialWidget();
        collapsibleContent.add(materialWidget).grow().row();
    }

    public void setMaterial(MaterialAsset material) {
        this.material = material;
        materialWidget.setMaterial(this.material);
    }

    @Override
    public void onDelete() {
        // can't be deleted
    }

    @Override
    public void setValues(GameObject go) {
        // nope
    }

}
