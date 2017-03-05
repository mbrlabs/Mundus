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
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneChangedEvent
import com.mbrlabs.mundus.editor.ui.widgets.ColorPickerField

/**
 * @author Marcus Brummer
 * *
 * @version 04-03-2016
 */
class AmbientLightDialog : BaseDialog("Ambient Light"), ProjectChangedEvent.ProjectChangedListener,
        SceneChangedEvent.SceneChangedListener {

    private val intensity = VisTextField("0")
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

        root.add(VisLabel("Intensity: ")).left().padBottom(10f)
        root.add(intensity).fillX().expandX().padBottom(10f).row()
        root.add(VisLabel("Color")).growX().row()
        root.add(colorPickerField).left().fillX().expandX().colspan(2).row()
        resetValues()
    }

    private fun setupListeners() {
        val projectContext = projectManager.current()

        // intensity
        intensity.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val d = convert(intensity.text)
                if (d != null) {
                    projectContext.currScene.environment.ambientLight.intensity = d
                }
            }
        })

        // color
        colorPickerField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                projectContext.currScene.environment.ambientLight.color.set(color)
            }
        }

    }

    private fun resetValues() {
        val light = projectManager.current().currScene.environment.ambientLight
        intensity.text = light.intensity.toString()
        colorPickerField.color = light.color
    }

    private fun convert(input: String): Float? {
        try {
            if (input.isEmpty()) return null
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
