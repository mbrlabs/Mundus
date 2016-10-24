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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.kotcrab.vis.ui.widget.color.ColorPickerListener
import com.mbrlabs.mundus.editor.ui.UI

/**
 *
 * @author Marcus Brummer
 * @version 08-01-2016
 */
class ColorPickerField() : VisTable() {

    /**
     *
     */
    var selectedColor: Color = Color.WHITE.cpy()
        set(value) {
            field.set(value)
            textField.text = "#" + value.toString()
        }

    /**
     *
     */
    var colorAdapter: ColorPickerAdapter? = null

    private val colorPickerListenerInternal: ColorPickerListener
    private val textField: VisTextField = VisTextField()
    private val cpBtn: VisTextButton = VisTextButton("Select")

    init {
        // setup internal color picker listener
        colorPickerListenerInternal = object : ColorPickerListener {
            override fun canceled(oldColor: Color?) {
                colorAdapter?.canceled(oldColor)
            }
            override fun reset(previousColor: Color?, newColor: Color?) {
                colorAdapter?.reset(previousColor, newColor)
            }
            override fun changed(newColor: Color?) {
                colorAdapter?.changed(newColor)
            }
            override fun finished(newColor: Color) {
                selectedColor = newColor
                colorAdapter?.finished(newColor)
            }
        }

        setEditable(false)
        setupUI()
        setupListeners()
    }

    /**
     *
     */
    fun setEditable(editable: Boolean) {
        textField.isDisabled = !editable
    }

    /**
     *
     */
    fun setDisabled(disable: Boolean) {
        cpBtn.isDisabled = disable
        if (disable) {
            cpBtn.touchable = Touchable.disabled
        } else {
            cpBtn.touchable = Touchable.enabled
        }
    }

    private fun setupUI() {
        add<VisTextField>(textField).padRight(5f).fillX().expandX()
        add(cpBtn).row()
    }

    private fun setupListeners() {
        // selectedColor chooser button
        cpBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                val colorPicker = UI.colorPicker
                colorPicker.color = selectedColor
                colorPicker.listener = colorPickerListenerInternal
                UI.addActor(colorPicker.fadeIn())
            }
        })

    }

}
