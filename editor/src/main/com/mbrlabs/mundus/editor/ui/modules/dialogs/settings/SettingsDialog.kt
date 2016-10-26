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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.settings

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTree
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
class SettingsDialog : BaseDialog("Settings") {

    private val settingsTree = VisTree()
    private val content = VisTable()
    private val saveBtn = VisTextButton("Save")
    private var listener: ClickListener? = null

    private val generalSettings = GeneralSettingsTable()
    private val exportSettings = ExportSettingsTable()
    private val appearenceSettings = AppearanceSettingsTable()

    init {
        val width = 700f
        val height = 400f
        val root = VisTable()
        content.padRight(UI.PAD_SIDE)
        add(root).width(width).height(height).row()

        root.add(settingsTree).width(width*0.3f).padRight(UI.PAD_SIDE).grow()
        root.addSeparator(true).padLeft(5f).padRight(5f)
        root.add(content).width(width*0.7f).grow().row()

        // general
        val generalSettingsNode = Tree.Node(VisLabel("General"))
        generalSettingsNode.`object` = generalSettings
        settingsTree.add(generalSettingsNode)

        // export
        val exportSettingsNode = Tree.Node(VisLabel("Export"))
        exportSettingsNode.`object` = exportSettings
        settingsTree.add(exportSettingsNode)

        // appearance
        val appearenceNode = Tree.Node(VisLabel("Appearance"))
        appearenceNode.`object` = appearenceSettings
        settingsTree.add(appearenceNode)

        // listener
        settingsTree.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val node = settingsTree.getNodeAt(y)
                replaceContent(node?.`object` as? BaseSettingsTable)
            }
        })

        // set initial content
        settingsTree.selection.add(generalSettingsNode)
        replaceContent(generalSettings)
    }

    private fun replaceContent(table: BaseSettingsTable?) {
        if(table == null) return
        content.clear()
        content.add(table).grow().row()
        content.add(saveBtn).growX().bottom().pad(10f).row()

        if(listener != null) {
            saveBtn.removeListener(listener!!)
        }
        listener = object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                table.onSave()
            }
        }
        saveBtn.addListener(listener)
    }

}
