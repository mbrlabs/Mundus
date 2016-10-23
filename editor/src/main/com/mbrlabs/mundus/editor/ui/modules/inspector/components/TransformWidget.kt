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

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.history.commands.RotateCommand
import com.mbrlabs.mundus.editor.history.commands.ScaleCommand
import com.mbrlabs.mundus.editor.history.commands.TranslateCommand
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.utils.formatFloat

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
class TransformWidget : BaseInspectorWidget("Transformation") {

    companion object {
        private val tempV3 = Vector3()
        private val tempQuat = Quaternion()
    }

    private val posX: FloatFieldWithLabel
    private val posY: FloatFieldWithLabel
    private val posZ: FloatFieldWithLabel

    private val rotX: FloatFieldWithLabel
    private val rotY: FloatFieldWithLabel
    private val rotZ: FloatFieldWithLabel

    private val scaleX: FloatFieldWithLabel
    private val scaleY: FloatFieldWithLabel
    private val scaleZ: FloatFieldWithLabel

    private val history: CommandHistory = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        val size = 65
        posX = FloatFieldWithLabel("x", size)
        posY = FloatFieldWithLabel("y", size)
        posZ = FloatFieldWithLabel("z", size)
        rotX = FloatFieldWithLabel("x", size)
        rotY = FloatFieldWithLabel("y", size)
        rotZ = FloatFieldWithLabel("z", size)
        scaleX = FloatFieldWithLabel("x", size)
        scaleY = FloatFieldWithLabel("y", size)
        scaleZ = FloatFieldWithLabel("z", size)

        isDeletable = false
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val pad = 4
        collapsibleContent.add(VisLabel("Position: ")).padRight(5f).padBottom(pad.toFloat()).left()
        collapsibleContent.add<FloatFieldWithLabel>(posX).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add<FloatFieldWithLabel>(posY).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add<FloatFieldWithLabel>(posZ).padBottom(pad.toFloat()).row()

        collapsibleContent.add(VisLabel("Rotation: ")).padRight(5f).padBottom(pad.toFloat()).left()
        collapsibleContent.add<FloatFieldWithLabel>(rotX).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add<FloatFieldWithLabel>(rotY).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add<FloatFieldWithLabel>(rotZ).padBottom(pad.toFloat()).row()

        collapsibleContent.add(VisLabel("Scale: ")).padRight(5f).padBottom(pad.toFloat()).left()
        collapsibleContent.add<FloatFieldWithLabel>(scaleX).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add<FloatFieldWithLabel>(scaleY).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add<FloatFieldWithLabel>(scaleZ).padBottom(pad.toFloat()).row()
    }

    private fun setupListeners() {
        val projectContext = projectManager.current()

        // position
        posX.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val go = projectContext.currScene.currentSelection ?: return
                val command = TranslateCommand(go)
                val pos = go.getLocalPosition(tempV3)
                command.setBefore(pos)
                go.setLocalPosition(posX.float, pos.y, pos.z)
                command.setAfter(go.getLocalPosition(tempV3))
                history.add(command)
            }
        })
        posY.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val go = projectContext.currScene.currentSelection ?: return
                val command = TranslateCommand(go)
                val pos = go.getLocalPosition(tempV3)
                command.setBefore(pos)
                go.setLocalPosition(pos.x, posY.float, pos.z)
                command.setAfter(go.getLocalPosition(tempV3))
                history.add(command)
            }
        })
        posZ.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val go = projectContext.currScene.currentSelection ?: return
                val command = TranslateCommand(go)
                val pos = go.getLocalPosition(tempV3)
                command.setBefore(pos)
                go.setLocalPosition(pos.x, pos.y, posZ.float)
                command.setAfter(go.getLocalPosition(tempV3))
                history.add(command)
            }
        })

        // rotation
        rotX.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val go = projectContext.currScene.currentSelection ?: return
                val rot = go.getLocalRotation(tempQuat)
                val rotateCommand = RotateCommand(go)
                rotateCommand.setBefore(rot)
                rot.setEulerAngles(rot.yaw, rotX.float, rot.roll)
                go.setLocalRotation(rot.x, rot.y, rot.z, rot.w)
                rotateCommand.setAfter(go.getLocalRotation(tempQuat))
                history.add(rotateCommand)
            }
        })
        rotY.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val go = projectContext.currScene.currentSelection ?: return
                val rot = go.getLocalRotation(tempQuat)
                val rotateCommand = RotateCommand(go)
                rotateCommand.setBefore(rot)
                rot.setEulerAngles(rotY.float, rot.pitch, rot.roll)
                go.setLocalRotation(rot.x, rot.y, rot.z, rot.w)
                rotateCommand.setAfter(go.getLocalRotation(tempQuat))
                history.add(rotateCommand)
            }
        })
        rotZ.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val go = projectContext.currScene.currentSelection ?: return
                val rot = go.getLocalRotation(tempQuat)
                val rotateCommand = RotateCommand(go)
                rotateCommand.setBefore(rot)
                rot.setEulerAngles(rot.yaw, rot.pitch, rotZ.float)
                go.setLocalRotation(rot.x, rot.y, rot.z, rot.w)
                rotateCommand.setAfter(go.getLocalRotation(tempQuat))
                history.add(rotateCommand)
            }
        })

        // scale
        scaleX.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val go = projectContext.currScene.currentSelection
                if (go != null && scaleX.float > 0f) {
                    val command = ScaleCommand(go)
                    val scl = go.getLocalScale(tempV3)
                    command.setBefore(scl)
                    go.setLocalScale(scaleX.float, scl.y, scl.z)
                    command.setAfter(go.getLocalScale(tempV3))
                    history.add(command)
                }
            }
        })
        scaleY.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val go = projectContext.currScene.currentSelection
                if (go != null && scaleY.float > 0f) {
                    val command = ScaleCommand(go)
                    val scl = go.getLocalScale(tempV3)
                    command.setBefore(scl)
                    go.setLocalScale(scl.x, scaleY.float, scl.z)
                    command.setAfter(go.getLocalScale(tempV3))
                    history.add(command)
                }
            }
        })
        scaleZ.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val go = projectContext.currScene.currentSelection
                if (go != null && scaleZ.float > 0f) {
                    val command = ScaleCommand(go)
                    val scl = go.getLocalScale(tempV3)
                    command.setBefore(scl)
                    go.setLocalScale(scl.x, scl.y, scaleZ.float)
                    command.setAfter(go.getLocalScale(tempV3))
                    history.add(command)
                }
            }
        })

    }

    override fun setValues(go: GameObject) {
        val pos = go.getLocalPosition(tempV3)
        posX.text = formatFloat(pos.x, 2)
        posY.text = formatFloat(pos.y, 2)
        posZ.text = formatFloat(pos.z, 2)

        val rot = go.getLocalRotation(tempQuat)
        rotX.text = formatFloat(rot.pitch, 2)
        rotY.text = formatFloat(rot.yaw, 2)
        rotZ.text = formatFloat(rot.roll, 2)

        val scl = go.getLocalScale(tempV3)
        scaleX.text = formatFloat(scl.x, 2)
        scaleY.text = formatFloat(scl.y, 2)
        scaleZ.text = formatFloat(scl.z, 2)
    }

    override fun onDelete() {
        // The transform component can't be deleted.
        // Every game object has a transformation
    }

}
