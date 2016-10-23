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

package com.mbrlabs.mundus.editor.ui.modules.inspector.assets

import com.kotcrab.vis.ui.widget.VisLabel
import com.mbrlabs.mundus.commons.assets.TerrainAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget

/**
 * @author Marcus Brummer
 * @version 15-10-2016
 */
class TerrainAssetInspectorWidget : BaseInspectorWidget(TerrainAssetInspectorWidget.TITLE) {

    companion object {
        private val TITLE = "Terrain Asset"
    }

    private val name: VisLabel
    private var terrain: TerrainAsset? = null

    init {
        name = VisLabel()
        collapsibleContent.add(name).growX().row()
    }

    fun setTerrainAsset(asset: TerrainAsset) {
        this.terrain = asset
        updateUI()
    }

    private fun updateUI() {
        name.setText("Name: " + terrain!!.name)
    }

    override fun onDelete() {

    }

    override fun setValues(go: GameObject) {

    }

}
