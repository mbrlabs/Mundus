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

import com.badlogic.gdx.Input
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.ui.UI

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
class ShortcutController(registry: Registry, private val projectManager: ProjectManager, private val history: CommandHistory)
    : KeyboardLayoutInputAdapter(registry) {

    private var isCtrlPressed = false

    override fun keyDown(keycode: Int): Boolean {
        val keycode = convertKeycode(keycode)

        // export
        if(keycode == Input.Keys.F1) {
            UI.exportDialog.export()
            return true
        }

        // CTR + xyz shortcuts

        if (keycode == Input.Keys.CONTROL_LEFT) {
            isCtrlPressed = true
        }
        if (!isCtrlPressed) return false

        if (keycode == Input.Keys.Z) {
            history.goBack()
            return true
        } else if (keycode == Input.Keys.Y) {
            history.goForward()
            return true
        } else if (keycode == Input.Keys.S) {
            projectManager.saveCurrentProject()
            UI.toaster.success("Project saved")
            return true
        }

        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        var keycode = keycode
        keycode = convertKeycode(keycode)
        if (keycode == Input.Keys.CONTROL_LEFT) {
            isCtrlPressed = false
        }
        return false
    }

}
