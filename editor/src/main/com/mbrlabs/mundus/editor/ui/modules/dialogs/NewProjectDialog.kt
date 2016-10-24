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

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
class NewProjectDialog : BaseDialog("Create New Project") {

    private val projectName = VisTextField()
    private val createBtn = VisTextButton("Create")
    private val locationPath = FileChooserField(300)

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        isModal = true

        val root = VisTable()
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root)

        root.add(VisLabel("Project Name:")).right().padRight(5f)
        root.add(this.projectName).height(21f).width(300f).fillX()
        root.row().padTop(10f)
        root.add(VisLabel("Location:")).right().padRight(5f)
        locationPath.setFileMode(FileChooser.SelectionMode.DIRECTORIES)
        root.add(locationPath).row()

        root.add(createBtn).width(93f).height(25f).colspan(2)

        setupListeners()

    }

    private fun setupListeners() {

        createBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val name = projectName.text
                var path = locationPath.path
                if (validateInput(name, path)) {
                    if (!path.endsWith("/")) {
                        path += "/"
                    }
                    val projectContext = projectManager.createProject(name, path)
                    close()
                    UI.loadingProjectDialog.loadProjectAsync(projectContext)
                }

            }
        })

    }

    private fun validateInput(name: String?, path: String?): Boolean {
        return name != null && name.length > 0 && path != null && path.length > 0
    }

}
