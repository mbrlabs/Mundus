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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.assets

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetFilter
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog

/**
 * @author Marcus Brummer
 * *
 * @version 02-10-2016
 */
class AssetSelectionDialog : BaseDialog(AssetSelectionDialog.TITLE),
        AssetImportEvent.AssetImportListener,
        ProjectChangedEvent.ProjectChangedListener {

    companion object {
        private val TAG = AssetSelectionDialog::class.java.simpleName
        private val TITLE = "Select an asset"
    }

    private val root: VisTable
    private val list: ListView<Asset>
    private val listAdapter: SimpleListAdapter<Asset>
    private val noneBtn: VisTextButton

    private var filter: AssetFilter? = null
    private var listener: AssetSelectionListener? = null

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)

        root = VisTable()
        listAdapter = SimpleListAdapter(Array<Asset>())
        list = ListView(listAdapter)
        noneBtn = VisTextButton("None / Remove old asset")

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        root.add(list.mainTable).grow().size(300f, 400f).row()
        root.add<VisTextButton>(noneBtn).grow().row()
        add<VisTable>(root).padRight(5f).padBottom(5f).grow().row()
    }

    private fun setupListeners() {
        list.setItemClickListener { item ->
            if (listener != null) {
                listener!!.onSelected(item)
                close()
            }
        }

        noneBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (listener != null) {
                    listener!!.onSelected(null)
                    close()
                }
            }
        })
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        reloadData()
    }

    override fun onAssetImported(event: AssetImportEvent) {
        reloadData()
    }

    private fun reloadData() {
        val assetManager = projectManager.current().assetManager
        listAdapter.clear()

        // filter assets
        for (asset in assetManager.assets) {
            if (filter != null) {
                if (filter!!.ignore(asset)) {
                    continue
                }
            }
            listAdapter.add(asset)
            Pixmap.setBlending(Pixmap.Blending.None)
        }

        listAdapter.itemsDataChanged()
    }

    fun show(showNoneAsset: Boolean, filter: AssetFilter, listener: AssetSelectionListener) {
        this.listener = listener
        this.filter = filter
        if (showNoneAsset) {
            noneBtn.isDisabled = false
            noneBtn.touchable = Touchable.enabled
        } else {
            noneBtn.isDisabled = true
            noneBtn.touchable = Touchable.disabled
        }
        reloadData()
        UI.showDialog(this)
    }

    /**
     */
    interface AssetSelectionListener {
        fun onSelected(asset: Asset?)
    }


}
