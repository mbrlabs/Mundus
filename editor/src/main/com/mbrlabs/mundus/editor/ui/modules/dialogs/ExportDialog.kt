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

import com.kotcrab.vis.ui.util.async.AsyncTaskListener
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisProgressBar
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.exporter.Exporter
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.Log
import com.mbrlabs.mundus.editor.utils.Toaster

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
class ExportDialog : VisDialog("Exporting") {

    private var lastExport: Long = 0

    private val label = VisLabel()
    private val progressBar = VisProgressBar(0f, 100f, 1f, false)

    private val projectManager: ProjectManager = Mundus.inject()
    private val kryoManager: KryoManager = Mundus.inject()

    init {
        isModal = true
        isMovable = false

        contentTable.add(label).padBottom(UI.PAD_BOTTOM).padTop(UI.PAD_BOTTOM_X2).left().growX().row()
        contentTable.add(progressBar).width(300f).left().growX().row()
    }

    fun export() {
        // validate
        val export = projectManager.current().settings?.export
        if(export == null || export.outputFolder == null || export.outputFolder.path().length == 0) {
            UI.toaster.error("Export Error\nYou have to supply a output folder in the export settings." +
                    "\nWindow -> Settings -> Export Settings")
            return
        }

        // prevent from exporting to fast which sometimes results in the export dialog not closing correctly
        if(System.currentTimeMillis() - lastExport < 1000f) {
            UI.toaster.error("Export pending")
            return
        }

        show(UI)

        Exporter(kryoManager, projectManager.current()).exportAsync(export.outputFolder, object: AsyncTaskListener {
            private var error = false

            override fun progressChanged(newProgressPercent: Int) {
                progressBar.value = newProgressPercent.toFloat()
            }

            override fun finished() {
                if(!error) {
                    UI.toaster.success("Project exported")
                }
                resetValues()
                close()
                lastExport = System.currentTimeMillis()
            }

            override fun messageChanged(message: String?) {
                label.setText(message)
            }

            override fun failed(message: String?, exception: Exception?) {
                Log.exception("Exporter", exception)
                UI.toaster.sticky(Toaster.ToastType.ERROR, "Export failed: " + exception.toString())
                error = true
                resetValues()
                close()
            }
        })
    }

    private fun resetValues() {
        progressBar.value = 0f
        label.setText("")
    }

    override fun close() {
        super.close()
    }

}
