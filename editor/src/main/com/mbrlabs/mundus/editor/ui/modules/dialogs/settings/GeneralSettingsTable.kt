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

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.core.registry.KeyboardLayout
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField

/**
 * @author Marcus Brummer
 * @version 29-02-2016
 */
class GeneralSettingsTable : BaseSettingsTable() {

    private val fbxBinary = FileChooserField(500)
    private val keyboardLayouts = VisSelectBox<KeyboardLayout>()

    private val kryoManager: KryoManager = Mundus.inject()
    private val registry: Registry = Mundus.inject()

    init {
        top().left()
        padRight(UI.PAD_SIDE).padLeft(UI.PAD_SIDE)

        add(VisLabel("General Settings")).left().row()
        addSeparator().padBottom(UI.PAD_SIDE*2)
        add(VisLabel("fbx-conv binary")).left().row()
        add(fbxBinary).growX().padBottom(UI.PAD_BOTTOM).row()

        keyboardLayouts.setItems(KeyboardLayout.QWERTY, KeyboardLayout.QWERTZ)
        keyboardLayouts.selected = registry.settings.keyboardLayout

        add(VisLabel("Keyboard Layout")).growX().row()
        add(keyboardLayouts).growX().row()

        addHandlers()
        reloadSettings()
    }



    fun reloadSettings() {
        fbxBinary.setText(registry.settings.fbxConvBinary)
    }

    private fun addHandlers() {
        keyboardLayouts.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val selection = keyboardLayouts.selected
                registry.settings.keyboardLayout = selection
            }
        })
    }

    override fun onSave() {
        val fbxPath = fbxBinary.path
        registry.settings.fbxConvBinary = fbxPath
        kryoManager.saveRegistry(registry)
        Mundus.postEvent(SettingsChangedEvent(registry.settings))
        UI.toaster.success("Settings saved")
    }

}
