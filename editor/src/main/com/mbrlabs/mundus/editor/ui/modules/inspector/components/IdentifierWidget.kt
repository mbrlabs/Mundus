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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
class IdentifierWidget : VisTable() {

    private val active = VisCheckBox("", true)
    private val name = VisTextField("Name")
    private val tag = VisTextField("Untagged")

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        add<VisCheckBox>(active).padBottom(4f).left().top()
        add<VisTextField>(name).padBottom(4f).left().top().expandX().fillX().row()
        add(VisLabel("Tag: ")).left().top()
        add<VisTextField>(tag).top().left().expandX().fillX().row()
    }

    private fun setupListeners() {
        val projectContext = projectManager.current()

        active.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                if (projectContext.currScene.currentSelection == null) return
                projectContext.currScene.currentSelection.active = active.isChecked
            }
        })

        name.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                if (projectContext.currScene.currentSelection == null) return
                projectContext.currScene.currentSelection.name = name.text
            }
        })

    }

    fun setValues(go: GameObject) {
        active.isChecked = go.active
        name.text = go.name
    }

}
