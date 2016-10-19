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

package com.mbrlabs.mundus.editor.ui.modules.inspector;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.MaterialAssetInspectorWidget;
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.ModelAssetInspectorWidget;
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.TerrainAssetInspectorWidget;
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.TextureAssetInspectorWidget;

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
public class AssetInspector extends VisTable {

    private MaterialAssetInspectorWidget materialWidget;
    private ModelAssetInspectorWidget modelWidget;
    private TextureAssetInspectorWidget textureWidget;
    private TerrainAssetInspectorWidget terrainWidget;

    private Asset asset;

    public AssetInspector() {
        super();
        align(Align.top);
        pad(7);

        materialWidget = new MaterialAssetInspectorWidget();
        modelWidget = new ModelAssetInspectorWidget();
        textureWidget = new TextureAssetInspectorWidget();
        terrainWidget = new TerrainAssetInspectorWidget();
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
        clear();
        if (asset instanceof MaterialAsset) {
            add(materialWidget).growX().row();
            materialWidget.setMaterial((MaterialAsset) asset);
        } else if (asset instanceof ModelAsset) {
            add(modelWidget).growX().row();
            modelWidget.setModel((ModelAsset) asset);
        } else if (asset instanceof TextureAsset) {
            add(textureWidget).growX().row();
            textureWidget.setTextureAsset((TextureAsset) asset);
        } else if (asset instanceof TerrainAsset) {
            add(terrainWidget).growX().row();
            terrainWidget.setTerrainAsset((TerrainAsset) asset);
        }

        // TODO other assets
    }

}
