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

package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager

/**
 * @version 09-10-2016
 * @author attilabo
 */
class ExitDialog : BaseDialog(ExitDialog.TITLE) {

    private val projectManager: ProjectManager = Mundus.inject()

    private val exit: VisTextButton
    private val saveExit: VisTextButton
    private val cancel: VisTextButton

    init {
        exit = VisTextButton("Exit")
        saveExit = VisTextButton("Save and Exit")
        cancel = VisTextButton("Cancel")

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val root = Table()
        root.padTop(6f).padRight(6f).padBottom(10f)
        add(root)

        root.add(VisLabel("Do you really want to close Mundus?")).grow().center().colspan(3).padBottom(10f).row()
        root.add<VisTextButton>(cancel).padRight(5f).grow()
        root.add<VisTextButton>(exit).padRight(5f).grow()
        root.add<VisTextButton>(saveExit).grow().row()
    }

    private fun setupListeners() {
        // cancel
        cancel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                close()
            }
        })

        // exit
        exit.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        // save current project & exit
        saveExit.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                projectManager.saveCurrentProject()
                Gdx.app.exit()
            }
        })
    }

    companion object {

        private val TITLE = "Confirm exit"
    }

}
