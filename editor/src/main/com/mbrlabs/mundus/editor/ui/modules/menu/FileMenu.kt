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
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectAlreadyImportedException
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.core.project.ProjectOpenException
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.ui.UI

/**
 * @author Marcus Brummer
 * *
 * @version 22-11-2015
 */
class FileMenu : Menu("File") {

    private val newProject = MenuItem("New Project")
    private val importProject = MenuItem("Import Project")
    private val recentProjects = MenuItem("Recent Projects")
    private val saveProject = MenuItem("Save Project")
    private val exit = MenuItem("Exit")

    private val registry: Registry = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        newProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.N)
        importProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.O)
        saveProject.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.S)

        // setup recent projects
        val recentPrjectsPopup = PopupMenu()
        for (ref in registry.projects) {
            val pro = MenuItem(ref.name + " - [" + ref.path + "]")
            pro.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    try {
                        val projectContext = projectManager.loadProject(ref)
                        projectManager.changeProject(projectContext)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Dialogs.showErrorDialog(UI, "Could not open project")
                    }

                }
            })
            recentPrjectsPopup.addItem(pro)
        }
        recentProjects.subMenu = recentPrjectsPopup

        addItem(newProject)
        addItem(importProject)
        addItem(saveProject)
        addItem(recentProjects)
        addSeparator()
        addItem(exit)

        setupListeners()
    }

    private fun setupListeners() {
        newProject.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.showDialog(UI.newProjectDialog)
            }
        })

        importProject.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.fileChooser.setListener(object: SingleFileChooserListener() {
                    override fun selected(file: FileHandle) {
                        importNewProject(file)
                    }
                })
                UI.fileChooser.selectionMode = FileChooser.SelectionMode.DIRECTORIES
                UI.addActor(UI.fileChooser.fadeIn())
            }
        })
    }

    fun importNewProject(projectDir: FileHandle) {
        try {
            val context = projectManager.importProject(projectDir.path())
            projectManager.changeProject(context)
        } catch (e: ProjectAlreadyImportedException) {
            e.printStackTrace()
            Dialogs.showErrorDialog(UI, "This Project is already imported.")
        } catch (e: ProjectOpenException) {
            e.printStackTrace()
            Dialogs.showErrorDialog(UI, "This Project can't be opened.")
        }

    }

}
