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
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuItem
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneAddedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.Log

/**
 * @author Marcus Brummer
 * *
 * @version 23-12-2015
 */
class SceneMenu : Menu("Scenes"),
        ProjectChangedEvent.ProjectChangedListener,
        SceneAddedEvent.SceneAddedListener {

    companion object {
        private val TAG = SceneMenu::class.java.simpleName
    }

    private val sceneItems = Array<MenuItem>()
    private val addScene: MenuItem

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)

        addScene = MenuItem("Add scene")
        addScene.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Dialogs.showInputDialog(UI, "Add Scene", "Name:", object : InputDialogAdapter() {
                    override fun finished(input: String?) {
                        val project = projectManager.current()
                        val scene = projectManager.createScene(project, input)
                        projectManager.changeScene(project, scene.name)
                        Mundus.postEvent(SceneAddedEvent(scene))
                    }
                })
            }
        })
        addItem(addScene)

        addSeparator()
        buildSceneUi()
    }

    private fun buildSceneUi() {
        // remove old items
        for (item in sceneItems) {
            removeActor(item)
        }
        // add new items
        for (scene in projectManager.current().scenes) {
            buildMenuItem(scene)
        }

    }

    private fun buildMenuItem(sceneName: String): MenuItem {
        val menuItem = MenuItem(sceneName)
        menuItem.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                projectManager.changeScene(projectManager.current(), sceneName)
            }
        })
        addItem(menuItem)
        sceneItems.add(menuItem)

        return menuItem
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        buildSceneUi()
    }

    override fun onSceneAdded(event: SceneAddedEvent) {
        val sceneName = event.scene!!.name
        buildMenuItem(sceneName)
        Log.trace(TAG, "SceneMenu", "New scene [{}] added.", sceneName)
    }

}
