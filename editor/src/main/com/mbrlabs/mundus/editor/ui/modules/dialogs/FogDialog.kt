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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
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
    private val colorPickerField = ColorPickerField()

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val root = Table()
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root)

        root.add(useFog).left().padBottom(10f).colspan(2).row()
        root.add(VisLabel("Density: ")).left().padBottom(10f)
        root.add(density).growX().padBottom(10f).row()
        root.add(VisLabel("Gradient: ")).left().padBottom(10f)
        root.add(gradient).growX().padBottom(10f).row()
        root.add(VisLabel("Color")).growX().row()
        root.add(colorPickerField).left().growX().colspan(2).row()
        resetValues()
    }

    private fun setupListeners() {

        // use fog checkbox
        useFog.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val projectContext = projectManager.current()
                if (useFog.isChecked) {
                    if (projectContext.currScene.environment.fog == null) {
                        val fog = Fog()
                        projectContext.currScene.environment.fog = fog
                        density.text = fog.density.toString()
                        gradient.text = fog.gradient.toString()
                    }
                    density.isDisabled = false
                    gradient.isDisabled = false
                    colorPickerField.disable(false)
                } else {
                    projectContext.currScene.environment.fog = null
                    density.isDisabled = true
                    gradient.isDisabled = true
                    colorPickerField.disable(true)
                }
            }
        })

        // gradient
        gradient.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val projectContext = projectManager.current()
                val g = convert(gradient.text)
                if (g != null) {
                    projectContext.currScene.environment.fog.gradient = g
                }
            }
        })

        // density
        density.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val projectContext = projectManager.current()
                val value = convert(density.text)
                if (value != null) {
                    projectContext.currScene.environment.fog.density = value
                }
            }
        })

        // color
        colorPickerField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                val projectContext = projectManager.current()
                projectContext.currScene.environment.fog.color.set(newColor)
            }
        }

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
            colorPickerField.selectedColor = fog.color
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
