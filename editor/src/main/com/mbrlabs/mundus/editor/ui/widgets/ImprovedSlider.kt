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

package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.utils.formatFloat

/**
 * Can be used inside a scroll pane & has the current value displayed on the
 * right.

 * @author Marcus Brummer
 * @version 04-02-2016
 */
class ImprovedSlider(min: Float, max: Float, step: Float) : VisTable() {

    private val currentValue = VisLabel("0")
    private val slider = ScrollPaneSlider(min, max, step, false)

    init {
        add(slider).expandX().fillX().left()
        add(currentValue).padLeft(10f).right()

        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                currentValue.setText(String.format(formatFloat(slider.value, 2)))
            }
        })
    }

    var value: Float
        get() = slider.value
        set(value) {
            slider.value = value
            currentValue.setText(formatFloat(value, 2))
        }

}
