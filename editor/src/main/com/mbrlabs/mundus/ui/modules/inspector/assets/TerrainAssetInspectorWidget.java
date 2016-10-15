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

import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.ui.modules.inspector.BaseInspectorWidget;

/**
 * @author Marcus Brummer
 * @version 15-10-2016
 */
public class TerrainAssetInspectorWidget extends BaseInspectorWidget {

    private static final String TITLE = "Terrain Asset";

    private VisLabel name;

    private TerrainAsset terrain;

    public TerrainAssetInspectorWidget() {
        super(TITLE);

        name = new VisLabel();
        collapsibleContent.add(name).growX().row();
    }

    public void setTerrainAsset(TerrainAsset asset) {
        this.terrain = asset;
        updateUI();
    }

    private void updateUI() {
        name.setText("Name: " + terrain.getName());
    }

    @Override
    public void onDelete() {

    }

    @Override
    public void setValues(GameObject go) {

    }

}
