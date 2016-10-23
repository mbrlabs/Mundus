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

package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.editor.assets.AssetFilter
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetSelectionDialog

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class AssetSelectionField : VisTable() {

    private val textField: VisTextField
    private val btn: VisTextButton

    private var listener: AssetSelectionDialog.AssetSelectionListener? = null
    private var filter: AssetFilter? = null

    private val internalListener: AssetSelectionDialog.AssetSelectionListener

    init {
        textField = VisTextField()
        textField.isDisabled = true
        btn = VisTextButton("Select")

        add(textField).grow()
        add(btn).padLeft(5f).row()

        internalListener = object: AssetSelectionDialog.AssetSelectionListener {
            override fun onSelected(asset: Asset?) {
                setAsset(asset)
                if (listener != null) {
                    listener!!.onSelected(asset)
                }
            }
        }

        btn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.assetSelectionDialog.show(true, filter!!, internalListener)
            }
        })
    }

    fun setListener(listener: AssetSelectionDialog.AssetSelectionListener): AssetSelectionField {
        this.listener = listener
        return this
    }

    fun setFilter(filter: AssetFilter): AssetSelectionField {
        this.filter = filter
        return this
    }

    fun setAsset(asset: Asset?) {
        textField.text = if (asset == null) "None" else asset.name
    }

}
