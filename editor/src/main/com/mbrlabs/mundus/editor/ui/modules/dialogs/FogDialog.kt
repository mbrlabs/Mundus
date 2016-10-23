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

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.env.Fog
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneChangedEvent
import com.mbrlabs.mundus.editor.ui.widgets.ColorPickerField

/**
 * @author Marcus Brummer
 * @version 06-01-2016
 */
class FogDialog : BaseDialog("Fog"), ProjectChangedEvent.ProjectChangedListener, SceneChangedEvent.SceneChangedListener {

    private val useFog = VisCheckBox("Use fog")
    private val density = VisTextField("0")
    private val gradient = VisTextField("0")
    private val colorPickerField = ColorPickerField("Color: ")

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val root = Table()
        // root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root)

        root.add(useFog).left().padBottom(10f).colspan(2).row()
        root.add(VisLabel("Density: ")).left().padBottom(10f)
        root.add(density).fillX().expandX().padBottom(10f).row()
        root.add(VisLabel("Gradient: ")).left().padBottom(10f)
        root.add(gradient).fillX().expandX().padBottom(10f).row()
        root.add(colorPickerField).left().fillX().expandX().colspan(2).row()
        resetValues()
    }

    private fun setupListeners() {
        val projectContext = projectManager.current()

        // use fog checkbox
        useFog.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                if (useFog.isChecked) {
                    if (projectContext.currScene.environment.fog == null) {
                        val fog = Fog()
                        projectContext.currScene.environment.fog = fog
                        density.text = fog.density.toString()
                        gradient.text = fog.gradient.toString()
                    }
                    density.isDisabled = false
                    gradient.isDisabled = false
                    colorPickerField.setDisabled(false)
                } else {
                    projectContext.currScene.environment.fog = null
                    density.isDisabled = true
                    gradient.isDisabled = true
                    colorPickerField.setDisabled(true)
                }
            }
        })

        // gradient
        gradient.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val g = convert(gradient.text)
                if (g != null) {
                    projectContext.currScene.environment.fog.gradient = g
                }
            }
        })

        // density
        density.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val d = convert(density.text)
                if (d != null) {
                    projectContext.currScene.environment.fog.density = d
                }
            }
        })

        // color
        colorPickerField.setCallback { color -> projectContext.currScene.environment.fog.color.set(color) }
    }

    private fun resetValues() {
        val fog = projectManager.current().currScene.environment.fog
        if (fog == null) {
            density.isDisabled = true
            gradient.isDisabled = true
        } else {
            useFog.isChecked = true
            density.text = fog.density.toString()
            gradient.text = fog.gradient.toString()
            colorPickerField.color = fog.color
        }
    }

    private fun convert(input: String): Float? {
        try {
            if (input.length == 0) return null
            return java.lang.Float.valueOf(input)
        } catch (e: Exception) {
            return null
        }

    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        resetValues()
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        resetValues()
    }

}
