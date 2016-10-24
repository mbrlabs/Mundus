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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.MenuBar
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisTable

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
class MundusMenuBar : MenuBar() {

    val fileMenu = FileMenu()
    val editMenu = EditMenu()
    val windowMenu = WindowMenu()
    val assetsMenu = AssetsMenu()
    val environmentMenu = EnvironmentMenu()
    private val sceneMenu = SceneMenu()

    init {
        addMenu(fileMenu)
        addMenu(editMenu)
        addMenu(assetsMenu)
        addMenu(environmentMenu)
        addMenu(sceneMenu)
        addMenu(windowMenu)
    }

    override fun getTable(): Table {
        val root = VisTable()
        root.setBackground("menu-bg")
        val menuTable = super.getTable()

        val icon = VisImage(Texture(Gdx.files.internal("ui/menu_icon.png")))
        root.add(icon).center().left().pad(5f)
        root.add(menuTable).expand().fill().left().center().row()
        val sep = VisTable()
        sep.background = VisUI.getSkin().getDrawable("mundus-separator-green")
        root.add(sep).expandX().fillX().height(1f).colspan(2).row()

        return root
    }

}
