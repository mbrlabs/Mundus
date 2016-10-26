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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
class SettingsDialog : BaseDialog("Settings") {

    private val settingsSelection = VisTable()
    private val content = VisTable()
    private val saveBtn = VisTextButton("Save")
    private var listener: ClickListener? = null

    private val generalBtn = VisTextButton("General")
    private val exportBtn = VisTextButton("Export")
    private val appearenceBtn = VisTextButton("Appearance")

    private val generalSettings = GeneralSettingsTable()
    private val exportSettings = ExportSettingsTable()
    private val appearenceSettings = AppearanceSettingsTable()

    init {
        val width = 700f
        val height = 400f
        val root = VisTable()
        add(root).width(width).height(height).row()

        root.add(settingsSelection).width(width*0.3f).grow().grow()
        root.addSeparator(true).padLeft(5f).padRight(5f)
        root.add(content).width(width*0.7f).grow().row()

        settingsSelection.align(Align.topLeft)
        settingsSelection.add(generalBtn).growX().pad(5f).row()
        settingsSelection.add(exportBtn).growX().pad(5f).row()
        settingsSelection.add(appearenceBtn).growX().pad(5f).row()

        setupListeners()
        replaceContent(generalSettings)
    }

    private fun setupListeners() {
        generalBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                replaceContent(generalSettings)
            }
        })

        appearenceBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                replaceContent(appearenceSettings)
            }
        })

        exportBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                replaceContent(exportSettings)
            }
        })
    }

    private fun replaceContent(table: BaseSettingsTable) {
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
