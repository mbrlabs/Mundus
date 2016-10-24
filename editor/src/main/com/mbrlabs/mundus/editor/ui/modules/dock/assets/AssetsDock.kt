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

package com.mbrlabs.mundus.editor.ui.modules.dock.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.commons.assets.*
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.ui.UI

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
class AssetsDock : Tab(false, false),
        ProjectChangedEvent.ProjectChangedListener,
        AssetImportEvent.AssetImportListener,
        GameObjectSelectedEvent.GameObjectSelectedListener {

    private val root = VisTable()
    private val filesViewContextContainer = VisTable(false)
    private val filesView = GridGroup(80f, 4f)

    private val assetItems = Array<AssetItem>()

    private val assetOpsMenu = PopupMenu()
    private val renameAsset = MenuItem("Rename Asset")
    private val deleteAsset = MenuItem("Delete Asset")

    private var currentSelection: AssetItem? = null
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)
        initUi()
    }

    fun initUi() {
        filesView.touchable = Touchable.enabled

        val contentTable = VisTable(false)
        contentTable.add(VisLabel("Assets")).left().padLeft(3f).row()
        contentTable.add(Separator()).padTop(3f).expandX().fillX()
        contentTable.row()
        contentTable.add<VisTable>(filesViewContextContainer).expandX().fillX()
        contentTable.row()
        contentTable.add(createScrollPane(filesView, true)).expand().fill()

        val splitPane = VisSplitPane(VisLabel("file tree here"), contentTable, false)
        splitPane.setSplitAmount(0.2f)

        root.setBackground("window-bg")
        root.add(splitPane).expand().fill()

        // asset ops right click menu
        assetOpsMenu.addItem(renameAsset)
        assetOpsMenu.addItem(deleteAsset)
    }

    private fun setSelected(assetItem: AssetItem?) {
        currentSelection = assetItem
        for (item in assetItems) {
            if (currentSelection != null && currentSelection == item) {
                item.background(VisUI.getSkin().getDrawable("default-select-selection"))
            } else {
                item.background(VisUI.getSkin().getDrawable("menu-bg"))
            }
        }
    }

    private fun reloadAssets() {
        filesView.clearChildren()
        val projectContext = projectManager.current()
        for (asset in projectContext.assetManager.assets) {
            val assetItem = AssetItem(asset)
            filesView.addActor(assetItem)
            assetItems.add(assetItem)
        }
    }

    private fun createScrollPane(actor: Actor, disableX: Boolean): VisScrollPane {
        val scrollPane = VisScrollPane(actor)
        scrollPane.setFadeScrollBars(false)
        scrollPane.setScrollingDisabled(disableX, false)
        return scrollPane
    }

    override fun getTabTitle(): String {
        return "Assets"
    }

    override fun getContentTable(): Table {
        return root
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        reloadAssets()
    }

    override fun onAssetImported(event: AssetImportEvent) {
        reloadAssets()
    }

    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
        setSelected(null)
    }

    /**
     * Asset item in the grid.
     */
    private inner class AssetItem(private val asset: Asset) : VisTable() {

        private val nameLabel: VisLabel

        init {
            setBackground("menu-bg")
            align(Align.center)
            nameLabel = VisLabel(asset.toString(), "tiny")
            nameLabel.setWrap(true)
            add(nameLabel).grow().top().row()

            addListener(object : InputListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    return true
                }

                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    if (event!!.button == Input.Buttons.RIGHT) {
                        assetOpsMenu.showMenu(UI, Gdx.input.x.toFloat(),
                                (Gdx.graphics.height - Gdx.input.y).toFloat())
                    } else if (event.button == Input.Buttons.LEFT) {
                        if (asset is MaterialAsset || asset is ModelAsset
                                || asset is TextureAsset || asset is TerrainAsset) {
                            this@AssetsDock.setSelected(this@AssetItem)
                            Mundus.postEvent(AssetSelectedEvent(asset))
                        }
                    }
                }

            })
        }
    }
}
