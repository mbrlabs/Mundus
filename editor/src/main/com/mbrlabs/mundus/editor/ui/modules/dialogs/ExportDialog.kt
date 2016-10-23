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

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener
import com.mbrlabs.mundus.editor.ui.UI
import java.io.File

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
class ExportDialog : BaseDialog("Export") {

    // UI elements
    private val fileChooser: FileChooser
    private val output = VisTextField()
    private val exportBtn = VisTextButton("EXPORT")
    private val fileChooserBtn = VisTextButton("Select")

    private val gzipCheckbox = VisCheckBox("Compress")
    private val prettyPrintCheckbox = VisCheckBox("Pretty print")

    init {
        isModal = true
        isMovable = false

        fileChooser = FileChooser(FileChooser.Mode.OPEN)
        fileChooser.selectionMode = FileChooser.SelectionMode.DIRECTORIES

        setupUI()
        setupListener()
    }

    private fun setupUI() {
        gzipCheckbox.left()
        prettyPrintCheckbox.left()
        val root = Table()
        // root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root).left().top()
        root.add(output).width(320f).padRight(7f).padBottom(5f).left()
        root.add(fileChooserBtn).width(80f).left().padBottom(5f).row()
        root.add(gzipCheckbox).width(400f).colspan(2).row()
        root.add(prettyPrintCheckbox).width(400f).colspan(2).row()
        root.add(exportBtn).width(400f).padTop(15f).colspan(2).row()

    }

    private fun setupListener() {

        // disable pretty print when compression is enabled
        gzipCheckbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                prettyPrintCheckbox.isDisabled = gzipCheckbox.isChecked
            }
        })

        // import btn
        exportBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                val folder = output.text
                if (validateInput(folder)) {
                    UI.toaster.error("Not implemented yet")
                }
            }
        })

        // button launches file chooser
        fileChooserBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                UI.addActor(fileChooser.fadeIn())
            }
        })

        // file chooser
        fileChooser.setListener(object : SingleFileChooserListener() {
            public override fun selected(file: FileHandle) {
                output.text = file.path()
            }
        })

    }

    private fun validateInput(folder: String): Boolean {
        val f = File(folder)
        if (!f.exists() || !f.isDirectory) {
            return false
        }

        return true
    }

    override fun close() {
        super.close()
    }

}
