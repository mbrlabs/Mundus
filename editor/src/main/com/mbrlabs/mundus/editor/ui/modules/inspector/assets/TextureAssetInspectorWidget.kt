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
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget

import org.apache.commons.io.FileUtils

/**
 * @author Marcus Brummer
 * @version 15-10-2016
 */
class TextureAssetInspectorWidget : BaseInspectorWidget(TextureAssetInspectorWidget.TITLE) {

    companion object {
        private val TITLE = "Texture Asset"
    }

    private val name = VisLabel()
    private val width = VisLabel()
    private val height = VisLabel()
    private val fileSize = VisLabel()

    private var textureAsset: TextureAsset? = null

    init {
        collapsibleContent.add(name).growX().row()
        collapsibleContent.add(width).growX().row()
        collapsibleContent.add(height).growX().row()
    }

    fun setTextureAsset(texture: TextureAsset) {
        this.textureAsset = texture
        updateUI()
    }

    private fun updateUI() {
        name.setText("Name: " + textureAsset?.name)
        width.setText("Width: " + textureAsset?.texture?.width + " px")
        height.setText("Height: " + textureAsset?.texture?.height + " px")

        val mb = FileUtils.sizeOf(textureAsset?.file?.file()) / 1000000f
        fileSize.setText("Size: $mb mb")
    }

    override fun onDelete() {
        // nope
    }

    override fun setValues(go: GameObject) {
        // nope
    }

}
