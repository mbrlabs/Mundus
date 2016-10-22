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

package com.mbrlabs.mundus.editor.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.IntIntMap

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
class FreeCamController : InputAdapter() {

    val SPEED_01 = 10f
    val SPEED_1 = 150f
    val SPEED_10 = 500f

    private var camera: Camera? = null
    private val keys = IntIntMap()
    private val STRAFE_LEFT = Input.Keys.A
    private val STRAFE_RIGHT = Input.Keys.D
    private val FORWARD = Input.Keys.W
    private val BACKWARD = Input.Keys.S
    private val UP = Input.Keys.Q
    private val DOWN = Input.Keys.E
    private var velocity = SPEED_1
    private var degreesPerPixel = 0.5f
    private val tmp = Vector3()

    fun setCamera(camera: Camera) {
        this.camera = camera
    }

    override fun keyDown(keycode: Int): Boolean {
        keys.put(keycode, keycode)
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        keys.remove(keycode, 0)
        return false
    }

    /**
     * Sets the velocity in units per second for moving forward, backward and
     * strafing left/right.
     *
     * @param velocity
     * *            the velocity in units per second
     */
    fun setVelocity(velocity: Float) {
        this.velocity = velocity
    }

    /**
     * Sets how many degrees to rotate per pixel the mouse moved.
     *
     * @param degreesPerPixel
     */
    fun setDegreesPerPixel(degreesPerPixel: Float) {
        this.degreesPerPixel = degreesPerPixel
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            val deltaX = -Gdx.input.deltaX * degreesPerPixel
            val deltaY = -Gdx.input.deltaY * degreesPerPixel
            camera!!.direction.rotate(camera!!.up, deltaX)
            tmp.set(camera!!.direction).crs(camera!!.up).nor()
            camera!!.direction.rotate(tmp, deltaY)
        }
        return false
    }

    @JvmOverloads fun update(deltaTime: Float = Gdx.graphics.deltaTime) {
        if (keys.containsKey(FORWARD)) {
            tmp.set(camera!!.direction).nor().scl(deltaTime * velocity)
            camera!!.position.add(tmp)
        }
        if (keys.containsKey(BACKWARD)) {
            tmp.set(camera!!.direction).nor().scl(-deltaTime * velocity)
            camera!!.position.add(tmp)
        }
        if (keys.containsKey(STRAFE_LEFT)) {
            tmp.set(camera!!.direction).crs(camera!!.up).nor().scl(-deltaTime * velocity)
            camera!!.position.add(tmp)
        }
        if (keys.containsKey(STRAFE_RIGHT)) {
            tmp.set(camera!!.direction).crs(camera!!.up).nor().scl(deltaTime * velocity)
            camera!!.position.add(tmp)
        }
        if (keys.containsKey(UP)) {
            tmp.set(camera!!.up).nor().scl(deltaTime * velocity)
            camera!!.position.add(tmp)
        }
        if (keys.containsKey(DOWN)) {
            tmp.set(camera!!.up).nor().scl(-deltaTime * velocity)
            camera!!.position.add(tmp)
        }
        camera!!.update(true)
    }
}
