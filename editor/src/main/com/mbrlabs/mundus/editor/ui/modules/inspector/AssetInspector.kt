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

    private val materialWidget = MaterialAssetInspectorWidget()
    private val modelWidget = ModelAssetInspectorWidget()
    private val textureWidget = TextureAssetInspectorWidget()
    private val terrainWidget = TerrainAssetInspectorWidget()

    var asset: Asset? = null
        set(value) {
            field = value
            clear()
            if (value is MaterialAsset) {
                add(materialWidget).growX().row()
                materialWidget.setMaterial(value)
            } else if (value is ModelAsset) {
                add(modelWidget).growX().row()
                modelWidget.setModel(value)
            } else if (value is TextureAsset) {
                add(textureWidget).growX().row()
                textureWidget.setTextureAsset(value)
            } else if (value is TerrainAsset) {
                add(terrainWidget).growX().row()
                terrainWidget.setTerrainAsset(value)
            }
        }

    init {
        align(Align.top)
        pad(7f)
    }

}
