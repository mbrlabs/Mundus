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

import com.badlogic.gdx.math.Quaternion
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.GameObjectModifiedEvent
import com.mbrlabs.mundus.editor.history.Command

/**
 * @author Marcus Brummer
 * @version 16-02-2016
 */
class RotateCommand(private val go: GameObject) : Command {

    companion object {
        private val modEvent = GameObjectModifiedEvent(null)
    }

    private var before: Quaternion
    private var after: Quaternion

    init {
        this.before = Quaternion()
        this.after = Quaternion()
    }

    fun setBefore(before: Quaternion) {
        this.before.set(before)
    }

    fun setAfter(after: Quaternion) {
        this.after.set(after)
    }

    override fun execute() {
        go.setLocalRotation(after.x, after.y, after.z, after.w)
        modEvent.gameObject = go
        Mundus.postEvent(modEvent)
    }

    override fun undo() {
        go.setLocalRotation(before.x, before.y, before.z, before.w)
        modEvent.gameObject = go
        Mundus.postEvent(modEvent)
    }

}
