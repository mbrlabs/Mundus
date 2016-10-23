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

package com.mbrlabs.mundus.editor.ui.modules.menu

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuItem
import com.mbrlabs.mundus.editor.ui.UI

/**
 * @author Marcus Brummer
 * *
 * @version 20-12-2015
 */
class EnvironmentMenu : Menu("Environment") {

    val ambientLight: MenuItem
    val skybox: MenuItem
    val fog: MenuItem

    init {
        ambientLight = MenuItem("Ambient Light")
        fog = MenuItem("Fog")
        skybox = MenuItem("Skybox")

        addItem(ambientLight)
        addItem(skybox)
        addItem(fog)

        setupListeners()
    }

    private fun setupListeners() {
        // ambient light
        ambientLight.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.showDialog(UI.ambientLightDialog)
            }
        })

        // fog
        fog.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.showDialog(UI.fogDialog)
            }
        })

        // skybox
        skybox.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.showDialog(UI.skyboxDialog)
            }
        })

    }

}
