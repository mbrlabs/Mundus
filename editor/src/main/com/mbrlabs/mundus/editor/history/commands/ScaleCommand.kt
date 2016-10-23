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

package com.mbrlabs.mundus.editor.history.commands

import com.badlogic.gdx.math.Vector3
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.GameObjectModifiedEvent
import com.mbrlabs.mundus.editor.history.Command

/**
 * @author Marcus Brummer
 * @version 20-02-2016
 */
class ScaleCommand(private var go: GameObject?) : Command {

    companion object {
        private val modEvent = GameObjectModifiedEvent(null)
    }

    private var before: Vector3
    private var after: Vector3

    init {
        this.before = Vector3()
        this.after = Vector3()
    }

    fun setBefore(before: Vector3) {
        this.before.set(before)
    }

    fun setAfter(after: Vector3) {
        this.after.set(after)
    }

    fun setGo(go: GameObject) {
        this.go = go
    }

    override fun execute() {
        go!!.setLocalScale(after.x, after.y, after.z)
        modEvent.gameObject = go
        Mundus.postEvent(modEvent)
    }

    override fun undo() {
        go!!.setLocalScale(before.x, before.y, before.z)
        modEvent.gameObject = go
        Mundus.postEvent(modEvent)
    }

}