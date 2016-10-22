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

import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.core.Mundus
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent
import com.mbrlabs.mundus.editor.history.Command
import com.mbrlabs.mundus.editor.utils.Log

/**
 * Delete command for game objects Deletion will update sceneGraph and outline
 *
 * @author codenigma
 * @version 28-09-2016
 */
class DeleteCommand(private var go: GameObject?, private var node: Tree.Node?) : Command {

    companion object {
        private val TAG = DeleteCommand::class.java.simpleName
    }

    private var parentGO: GameObject? = null
    private var parentNode: Tree.Node? = null
    private var tree: Tree? = null

    init {
        this.parentGO = go!!.parent
        this.parentNode = node!!.parent
        this.tree = node!!.tree
    }

    override fun execute() {
        Log.trace(TAG, "Remove game object [{}]", go)
        // remove go from sceneGraph
        go!!.remove()
        // remove from outline tree
        tree!!.remove(node!!)
        Mundus.postEvent(SceneGraphChangedEvent())
    }

    override fun undo() {
        Log.trace(TAG, "Undo remove of game object [{}]", go)
        // add to sceneGraph
        parentGO!!.addChild(go)
        // add to outline
        if (parentNode == null)
            tree!!.add(node)
        else
            parentNode!!.add(node)
        node!!.expandTo()
        Mundus.postEvent(SceneGraphChangedEvent())
    }

}
