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

package com.mbrlabs.mundus.editor.ui.modules.menu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuItem
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.history.CommandHistory

/**
 * @author Marcus Brummer
 * *
 * @version 22-11-2015
 */
class EditMenu : Menu("Edit") {

    private val copy: MenuItem
    private val paste: MenuItem
    private val undo: MenuItem
    private val redo: MenuItem

    private val history: CommandHistory = Mundus.inject()

    init {
        copy = MenuItem("Copy")
        copy.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.C)
        paste = MenuItem("Paste")
        paste.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.P)
        undo = MenuItem("Undo")
        undo.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.Z)
        redo = MenuItem("Redo")
        redo.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.Y)

        addItem(copy)
        addItem(paste)
        addItem(undo)
        addItem(redo)

        setupListeners()
    }

    private fun setupListeners() {
        // undo
        undo.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                history.goBack()
            }
        })

        // redo
        redo.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                history.goForward()
            }
        })

    }

}
