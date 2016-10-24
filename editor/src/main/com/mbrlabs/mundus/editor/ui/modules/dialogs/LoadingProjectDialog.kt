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

import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectContext
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.ui.UI
import java.io.File

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
class LoadingProjectDialog : VisDialog("Loading Project") {

    private val projectName = VisLabel("Project Folder:")
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        isModal = true
        isMovable = false

        val root = VisTable()
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root)

        root.add(projectName).right().padRight(5f)
    }

    fun loadProjectAsync(projectContext: ProjectContext) {
        this.projectName.setText("Loading project: " + projectContext.name)
        UI.showDialog(this)

        if (File(projectContext.path).exists()) {
            projectManager.changeProject(projectContext)
            close()
        } else {
            close()
            Dialogs.showErrorDialog(UI, "Faild to load project " + projectContext.path)
        }

    }

}
