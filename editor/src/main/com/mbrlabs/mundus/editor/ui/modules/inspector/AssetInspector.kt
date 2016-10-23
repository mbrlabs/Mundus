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

package com.mbrlabs.mundus.editor.ui.modules.inspector

import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.assets.*
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.MaterialAssetInspectorWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.ModelAssetInspectorWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.TerrainAssetInspectorWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.assets.TextureAssetInspectorWidget

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class AssetInspector : VisTable() {

    private val materialWidget: MaterialAssetInspectorWidget
    private val modelWidget: ModelAssetInspectorWidget
    private val textureWidget: TextureAssetInspectorWidget
    private val terrainWidget: TerrainAssetInspectorWidget

    private var asset: Asset? = null

    init {
        align(Align.top)
        pad(7f)

        materialWidget = MaterialAssetInspectorWidget()
        modelWidget = ModelAssetInspectorWidget()
        textureWidget = TextureAssetInspectorWidget()
        terrainWidget = TerrainAssetInspectorWidget()
    }

    fun setAsset(asset: Asset) {
        this.asset = asset
        clear()
        if (asset is MaterialAsset) {
            add(materialWidget).growX().row()
            materialWidget.setMaterial(asset)
        } else if (asset is ModelAsset) {
            add(modelWidget).growX().row()
            modelWidget.setModel(asset)
        } else if (asset is TextureAsset) {
            add(textureWidget).growX().row()
            textureWidget.setTextureAsset(asset)
        } else if (asset is TerrainAsset) {
            add(terrainWidget).growX().row()
            terrainWidget.setTerrainAsset(asset)
        }

        // TODO other assets
    }

}
