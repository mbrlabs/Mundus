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

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.ui.modules.inspector.assets.MaterialAssetInspectorWidget;

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
public class AssetInspector extends VisTable {

    private MaterialAssetInspectorWidget materialWidget;

    private Asset asset;

    public AssetInspector() {
        super();
        align(Align.top);
        pad(7);

        materialWidget = new MaterialAssetInspectorWidget();
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
        if (asset instanceof MaterialAsset) {
            clear();
            add(materialWidget).growX().row();
            materialWidget.setMaterial((MaterialAsset) asset);
        }
        // TODO other assets
    }

}
