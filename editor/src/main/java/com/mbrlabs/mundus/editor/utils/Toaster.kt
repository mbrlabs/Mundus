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

package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.util.ToastManager
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.toast.Toast

/**
 * Displays Android-like toasts at top right corner of the screen.
 *
 * @author Marcus Brummer
 * @version 07-06-2016
 */
class Toaster(stage: Stage) {

    /** Toast type determines background color of toast.  */
    enum class ToastType {
        SUCCESS, INFO, ERROR
    }

    private val toastManager: ToastManager

    init {
        this.toastManager = ToastManager(stage)
    }

    /**
     * Amber background.
     *
     * @param msg
     */
    fun info(msg: String) {
        val table = newTable(msg)
        val toast = Toast(ToastType.INFO.name.toLowerCase(), table)
        toastManager.show(toast, 3f)
    }

    /**
     * Red background
     *
     * @param msg
     */
    fun error(msg: String) {
        val table = newTable(msg)
        val toast = Toast(ToastType.ERROR.name.toLowerCase(), table)
        toastManager.show(toast, 5f)
    }

    /**
     * Teal background.
     *
     * @param msg
     */
    fun success(msg: String) {
        val table = newTable(msg)
        val toast = Toast(ToastType.SUCCESS.name.toLowerCase(), table)
        toastManager.show(toast, 3f)
    }

    /**
     * Does not close itself. User has to
     *
     * @param type
     * @param msg
     */
    fun sticky(type: ToastType, msg: String) {
        val table = newTable(msg)
        var toast: Toast? = null
        if (type == ToastType.SUCCESS) {
            toast = Toast(ToastType.SUCCESS.name.toLowerCase(), table)
        } else if (type == ToastType.INFO) {
            toast = Toast(ToastType.INFO.name.toLowerCase(), table)
        } else if (type == ToastType.ERROR) {
            toast = Toast(ToastType.ERROR.name.toLowerCase(), table)
        }

        toastManager.show(toast)
    }

    private fun newTable(text: String): VisTable {
        val table = VisTable()
        table.add(VisLabel(text))
        table.pad(5f)
        return table
    }

}
