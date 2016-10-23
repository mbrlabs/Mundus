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

package com.mbrlabs.mundus.editor.ui.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.input.FreeCamController
import com.mbrlabs.mundus.editor.utils.formatFloat

/**
 * @author Marcus Brummer
 * *
 * @version 24-11-2015
 */
class StatusBar : VisTable() {

    private val root: VisTable
    private val left: VisTable
    private val right: VisTable

    private val fpsLabel: VisLabel
    private val camPos: VisLabel

    private val speed01: VisTextButton
    private val speed1: VisTextButton
    private val speed10: VisTextButton

    private val freeCamController: FreeCamController = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        background = VisUI.getSkin().getDrawable("menu-bg")
        root = VisTable()
        root.align(Align.left or Align.center)
        add(root).expand().fill()

        left = VisTable()
        left.align(Align.left)
        left.padLeft(10f)
        right = VisTable()
        right.align(Align.right)
        right.padRight(10f)
        root.add(left).left().expand().fill()
        root.add(right).right().expand().fill()

        // left
        left.add(VisLabel("camSpeed: ")).left()
        speed01 = VisTextButton(".1")
        speed1 = VisTextButton("1")
        speed10 = VisTextButton("10")
        left.add(speed01)
        left.add(speed1)
        left.add(speed10)

        // right
        fpsLabel = VisLabel()
        camPos = VisLabel()
        right.add(camPos).right()
        right.addSeparator(true).padLeft(5f).padRight(5f)
        right.add(fpsLabel).right()

        setupListeners()
    }

    fun setupListeners() {
        speed01.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                freeCamController.setVelocity(freeCamController.SPEED_01)
            }
        })

        speed1.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                freeCamController.setVelocity(freeCamController.SPEED_1)
            }
        })

        speed10.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                freeCamController.setVelocity(freeCamController.SPEED_10)
            }
        })
    }

    override fun act(delta: Float) {
        setFps(Gdx.graphics.framesPerSecond)
        setCamPos(projectManager.current().currScene.cam.position)
        super.act(delta)
    }

    private fun setFps(fps: Int) {
        this.fpsLabel.setText("fps: " + fps)
    }

    private fun setCamPos(pos: Vector3) {
        camPos.setText("camPos: " + formatFloat(pos.x, 2) + ", " + formatFloat(pos.y, 2) + ", "
                + formatFloat(pos.z, 2))
    }

}
