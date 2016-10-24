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

import com.mbrlabs.mundus.commons.assets.MaterialAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget
import com.mbrlabs.mundus.editor.ui.widgets.MaterialWidget

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class MaterialAssetInspectorWidget : BaseInspectorWidget(MaterialAssetInspectorWidget.TITLE) {

    companion object {
        private val TITLE = "Material Asset"
    }

    private var material: MaterialAsset? = null
    private val materialWidget = MaterialWidget()

    init {
        isDeletable = false
        collapsibleContent.add(materialWidget).grow().row()
    }

    fun setMaterial(material: MaterialAsset) {
        this.material = material
        materialWidget.material = this.material
    }

    override fun onDelete() {
        // can't be deleted
    }

    override fun setValues(go: GameObject) {
        // nope
    }

}
