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
import com.mbrlabs.mundus.commons.skybox.Skybox
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneChangedEvent
import com.mbrlabs.mundus.editor.ui.widgets.ImageChooserField
import com.mbrlabs.mundus.editor.utils.createDefaultSkybox

/**
 * @author Marcus Brummer
 * @version 10-01-2016
 */
class SkyboxDialog : BaseDialog("Skybox"), ProjectChangedEvent.ProjectChangedListener, SceneChangedEvent.SceneChangedListener {

    private val positiveX: ImageChooserField = ImageChooserField(100)
    private var negativeX: ImageChooserField = ImageChooserField(100)
    private var positiveY: ImageChooserField = ImageChooserField(100)
    private var negativeY: ImageChooserField = ImageChooserField(100)
    private var positiveZ: ImageChooserField = ImageChooserField(100)
    private var negativeZ: ImageChooserField = ImageChooserField(100)

    private var createBtn: VisTextButton
    private var defaultBtn: VisTextButton
    private var deletBtn: VisTextButton

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)

        createBtn = VisTextButton("Create skybox")
        defaultBtn = VisTextButton("Create default skybox")
        deletBtn = VisTextButton("Remove Skybox")

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        positiveX.setButtonText("positiveX")
        negativeX.setButtonText("negativeX")
        positiveY.setButtonText("positiveY")
        negativeY.setButtonText("negativeY")
        positiveZ.setButtonText("positiveZ")
        negativeZ.setButtonText("negativeZ")

        val root = VisTable()
        // root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root).left().top()
        root.add(VisLabel("The 6 images must be square and of equal size")).colspan(3).row()
        root.addSeparator().colspan(3).row()
        root.add<ImageChooserField>(positiveX)
        root.add<ImageChooserField>(negativeX)
        root.add<ImageChooserField>(positiveY).row()
        root.add<ImageChooserField>(negativeY)
        root.add<ImageChooserField>(positiveZ)
        root.add<ImageChooserField>(negativeZ).row()
        root.add<VisTextButton>(createBtn).padTop(15f).padLeft(6f).padRight(6f).expandX().fillX().colspan(3).row()

        val tab = VisTable()
        tab.add<VisTextButton>(defaultBtn).expandX().padRight(3f).fillX()
        tab.add<VisTextButton>(deletBtn).expandX().fillX().padLeft(3f).row()
        root.add(tab).fillX().expandX().padTop(5f).padLeft(6f).padRight(6f).colspan(3).row()
    }

    private fun setupListeners() {
        val projectContext = projectManager.current()

        // create btn
        createBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val oldSkybox = projectContext.currScene.skybox
                oldSkybox?.dispose()

                projectContext.currScene.skybox = Skybox(positiveX.file, negativeX.file,
                        positiveY.file, negativeY.file, positiveZ.file, negativeZ.file)
                resetImages()
            }
        })

        // default skybox btn
        defaultBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (projectContext.currScene.skybox != null) {
                    projectContext.currScene.skybox.dispose()
                }
                projectContext.currScene.skybox = createDefaultSkybox()
                resetImages()
            }
        })

        // delete skybox btn
        deletBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                projectContext.currScene.skybox.dispose()
                projectContext.currScene.skybox = null
                resetImages()
            }
        })

    }

    private fun resetImages() {
        val skybox = projectManager.current().currScene.skybox
        if (skybox != null) {
            positiveX.setImage(skybox.positiveX)
            negativeX.setImage(skybox.negativeX)
            positiveY.setImage(skybox.positiveY)
            negativeY.setImage(skybox.negativeY)
            positiveZ.setImage(skybox.positiveY)
            negativeZ.setImage(skybox.negativeZ)
        } else {
            positiveX.setImage(null)
            negativeX.setImage(null)
            positiveY.setImage(null)
            negativeY.setImage(null)
            positiveZ.setImage(null)
            negativeZ.setImage(null)
        }
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        resetImages()
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        resetImages()
    }

}
