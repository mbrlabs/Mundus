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
package com.mbrlabs.mundus.editor.ui.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.*
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.*
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.history.commands.DeleteCommand
import com.mbrlabs.mundus.editor.shader.Shaders
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.Log
import com.mbrlabs.mundus.editor.utils.createTerrainGO

/**
 * Outline shows overview about all game objects in the scene
 *
 * @author Marcus Brummer, codenigma
 * @version 01-10-2016
 */
class Outline : VisTable(),
        ProjectChangedEvent.ProjectChangedListener,
        SceneChangedEvent.SceneChangedListener,
        SceneGraphChangedEvent.SceneGraphChangedListener,
        GameObjectSelectedEvent.GameObjectSelectedListener {

    private val content: VisTable
    private val tree: VisTree
    private val scrollPane: ScrollPane
    private val dragAndDrop: DragAndDrop = DragAndDrop()
    private val rightClickMenu: RightClickMenu

    private val toolManager: ToolManager = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()
    private val history: CommandHistory = Mundus.inject()

    init {
        Mundus.registerEventListener(this)
        setBackground("window-bg")

        rightClickMenu = RightClickMenu()

        content = VisTable()
        content.align(Align.left or Align.top)

        tree = VisTree()
        tree.selection.setProgrammaticChangeEvents(false)
        scrollPane = VisScrollPane(tree)
        scrollPane.setFlickScroll(false)
        scrollPane.setFadeScrollBars(false)
        content.add(scrollPane).fill().expand()

        add(VisLabel(TITLE)).expandX().fillX().pad(3f).row()
        addSeparator().row()
        add(content).fill().expand()

        setupDragAndDrop()
        setupListeners()
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        // update to new sceneGraph
        Log.trace(TAG, "Project changed. Building scene graph.")
        buildTree(projectManager.current().currScene.sceneGraph)
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        // update to new sceneGraph
        Log.trace(TAG, "Scene changed. Building scene graph.")
        buildTree(projectManager.current().currScene.sceneGraph)
    }

    override fun onSceneGraphChanged(event: SceneGraphChangedEvent) {
        Log.trace(TAG, "SceneGraph changed. Building scene graph.")
        buildTree(projectManager.current().currScene.sceneGraph)
    }

    private fun setupDragAndDrop() {
        // source
        dragAndDrop.addSource(object : DragAndDrop.Source(tree) {
            override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): DragAndDrop.Payload? {
                val payload = DragAndDrop.Payload()
                val node = tree.getNodeAt(y)
                if (node != null) {
                    payload.`object` = node
                    return payload
                }

                return null
            }
        })

        // target
        dragAndDrop.addTarget(object : DragAndDrop.Target(tree) {
            override fun drag(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int): Boolean {
                // Select node under mouse if not over the selection.
                val overNode = tree.getNodeAt(y)
                if (overNode == null && tree.selection.isEmpty) {
                    return true
                }
                if (overNode != null && !tree.selection.contains(overNode)) {
                    tree.selection.set(overNode)
                }
                return true
            }

            override fun drop(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int) {
                val node = payload.`object` as Tree.Node
                val context = projectManager.current()

                if (node != null) {
                    val draggedGo = node.`object` as GameObject
                    val newParent = tree.getNodeAt(y)

                    // check if a go is dragged in one of its' children or
                    // itself
                    if (newParent != null) {
                        val parentGo = newParent.`object` as GameObject
                        if (parentGo.isChildOf(draggedGo)) {
                            return
                        }
                    }
                    val oldParent = draggedGo.parent

                    // remove child from old parent
                    draggedGo.remove()

                    // add to new parent
                    if (newParent == null) {
                        // recalculate position for root layer
                        val newPos: Vector3
                        val draggedPos = Vector3()
                        draggedGo.getPosition(draggedPos)
                        // if moved from old parent
                        if (oldParent != null) {
                            // new position = oldParentPos + draggedPos
                            val parentPos = Vector3()
                            oldParent.getPosition(parentPos)
                            newPos = parentPos.add(draggedPos)
                        } else {
                            // new local position = World position
                            newPos = draggedPos
                        }
                        context.currScene.sceneGraph.addGameObject(draggedGo)
                        draggedGo.setLocalPosition(newPos.x, newPos.y, newPos.z)
                    } else {
                        val parentGo = newParent.`object` as GameObject
                        // recalculate position
                        val parentPos = Vector3()
                        var draggedPos = Vector3()
                        // World coorinates
                        draggedGo.getPosition(draggedPos)
                        parentGo.getPosition(parentPos)

                        // if gameObject came from old parent
                        if (oldParent != null) {
                            // calculate oldParentPos + draggedPos
                            val oldParentPos = Vector3()
                            oldParent.getPosition(oldParentPos)
                            draggedPos = oldParentPos.add(draggedPos)
                        }

                        // Local in releation to new parent
                        val newPos = draggedPos.sub(parentPos)
                        // add
                        parentGo.addChild(draggedGo)
                        draggedGo.setLocalPosition(newPos.x, newPos.y, newPos.z)
                    }

                    // update tree
                    buildTree(projectManager.current().currScene.sceneGraph)
                }
            }
        })
    }

    private fun setupListeners() {

        scrollPane.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                UI.scrollFocus = scrollPane
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                UI.scrollFocus = null
            }

        })

        // right click menu listener
        tree.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (Input.Buttons.RIGHT != button) {
                    return
                }

                val node = tree.getNodeAt(y)
                var go: GameObject? = null
                if (node != null) {
                    go = node.getObject() as GameObject
                }
                rightClickMenu.show(go, Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })

        // select listener
        tree.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val selection = tree.selection
                if (selection != null && selection.size() > 0) {
                    val go = selection.first().`object` as GameObject
                    projectManager.current().currScene.sceneGraph.selected = go
                    toolManager.translateTool.gameObjectSelected(go)
                    Mundus.postEvent(GameObjectSelectedEvent(go))
                }
            }
        })

    }

    /**
     * Building tree from game objects in sceneGraph, clearing previous
     * sceneGraph

     * @param sceneGraph
     */
    private fun buildTree(sceneGraph: SceneGraph) {
        tree.clearChildren()

        for (go in sceneGraph.gameObjects) {
            addGoToTree(null, go)
        }
    }

    /**
     * Adding game object to outline

     * @param treeParentNode
     * *
     * @param gameObject
     */
    private fun addGoToTree(treeParentNode: Tree.Node?, gameObject: GameObject) {
        val leaf = Tree.Node(TreeNode(gameObject))
        leaf.`object` = gameObject
        if (treeParentNode == null) {
            tree.add(leaf)
        } else {
            treeParentNode.add(leaf)
        }
        // Always expand after adding new node
        leaf.expandTo()
        if (gameObject.children != null) {
            for (goChild in gameObject.children) {
                addGoToTree(leaf, goChild)
            }
        }
    }

    /**
     * Removing game object from tree and outline

     * @param go
     */
    private fun removeGo(go: GameObject) {
        // run delete command, updating sceneGraph and outline
        val deleteCommand = DeleteCommand(go, tree.findNode(go))
        history.add(deleteCommand)
        deleteCommand.execute() // run delete
    }

    /**
     * Deep copy of all game objects

     * @param go
     * *            the game object for cloning, with children
     * *
     * @param parent
     * *            game object on which clone will be added
     */
    private fun duplicateGO(go: GameObject, parent: GameObject) {
        Log.trace(TAG, "Duplicate [{}] with parent [{}]", go, parent)
        val goCopy = GameObject(go, projectManager.current().obtainID())

        // add copy to tree
        val n = tree.findNode(parent)
        addGoToTree(n, goCopy)

        // add copy to scene graph
        parent.addChild(goCopy)

        // recursively clone child objects
        if (go.children != null) {
            for (child in go.children) {
                duplicateGO(child, goCopy)
            }
        }
    }

    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
        val node = tree.findNode(event.gameObject!!)
        Log.trace(TAG, "Select game object [{}].", node.`object`)
        tree.selection.clear()
        tree.selection.add(node)
        node.expandTo()
    }

    /**
     * A node of the ui tree hierarchy.
     */
    private inner class TreeNode(go: GameObject) : VisTable() {

        val nameLabel: VisLabel

        init {
            nameLabel = VisLabel()
            add(nameLabel).expand().fill()
            nameLabel.setText(go.name)
        }
    }

    /**

     */
    private inner class RightClickMenu : PopupMenu() {

        private val addEmpty: MenuItem
        private val addTerrain: MenuItem
        private val duplicate: MenuItem
        private val rename: MenuItem
        private val delete: MenuItem

        private var selectedGO: GameObject? = null

        init {

            addEmpty = MenuItem("Add Empty")
            addTerrain = MenuItem("Add terrain")
            duplicate = MenuItem("Duplicate")
            rename = MenuItem("Rename")
            delete = MenuItem("Delete")

            // add empty
            addEmpty.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val sceneGraph = projectManager.current().currScene.sceneGraph
                    val id = projectManager.current().obtainID()
                    // the new game object
                    val go = GameObject(sceneGraph, GameObject.DEFAULT_NAME, id)
                    // update outline
                    if (selectedGO == null) {
                        // update sceneGraph
                        Log.trace(TAG, "Add empty game object [{}] in root node.", go)
                        sceneGraph.addGameObject(go)
                        // update outline
                        addGoToTree(null, go)
                    } else {
                        Log.trace(TAG, "Add empty game object [{}] child in node [{}].", go, selectedGO)
                        // update sceneGraph
                        selectedGO!!.addChild(go)
                        // update outline
                        val n = tree.findNode(selectedGO!!)
                        addGoToTree(n, go)
                    }
                    Mundus.postEvent(SceneGraphChangedEvent())
                }
            })

            // add terrainAsset
            addTerrain.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    try {
                        Log.trace(TAG, "Add terrain game object in root node.")
                        val context = projectManager.current()
                        val sceneGraph = context.currScene.sceneGraph
                        val goID = projectManager.current().obtainID()
                        val name = "Terrain " + goID
                        // create asset
                        val asset = context.assetManager.createTerraAsset(name,
                                Terrain.DEFAULT_VERTEX_RESOLUTION, Terrain.DEFAULT_SIZE)
                        asset.load()
                        asset.applyDependencies()

                        val terrainGO = createTerrainGO(sceneGraph,
                                Shaders.terrainShader, goID, name, asset)
                        // update sceneGraph
                        sceneGraph.addGameObject(terrainGO)
                        // update outline
                        addGoToTree(null, terrainGO)

                        context.currScene.terrains.add(asset)
                        projectManager.saveProject(context)
                        Mundus.postEvent(AssetImportEvent(asset))
                        Mundus.postEvent(SceneGraphChangedEvent())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })

            rename.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null) {
                        showRenameDialog()
                    }
                }
            })

            // duplicate node
            duplicate.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null && !duplicate.isDisabled) {
                        duplicateGO(selectedGO!!, selectedGO!!.parent)
                        Mundus.postEvent(SceneGraphChangedEvent())
                    }
                }
            })

            // delete game object
            delete.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null) {
                        removeGo(selectedGO!!)
                        Mundus.postEvent(SceneGraphChangedEvent())
                    }
                }
            })

            addItem(addEmpty)
            addItem(addTerrain)
            addItem(rename)
            addItem(duplicate)
            addItem(delete)
        }

        /**
         * Right click event opens menu and enables more options if selected
         * game object is active.
         *
         * @param go
         * @param x
         * @param y
         */
        fun show(go: GameObject?, x: Float, y: Float) {
            selectedGO = go
            showMenu(UI, x, y)

            // check if game object is selected
            if (selectedGO != null) {
                // Activate menu options for selected game objects
                rename.isDisabled = false
                delete.isDisabled = false
            } else {
                // disable MenuItems which only works with selected item
                rename.isDisabled = true
                delete.isDisabled = true
            }

            // terrainAsset can not be duplicated
            if (selectedGO == null || selectedGO!!.findComponentByType(Component.Type.TERRAIN) != null) {
                duplicate.isDisabled = true
            } else {
                duplicate.isDisabled = false
            }
        }

        fun showRenameDialog() {
            val node = tree.findNode(selectedGO!!)
            val goNode = node.actor as TreeNode

            val renameDialog = Dialogs.showInputDialog(UI, "Rename", "",
                    object : InputDialogAdapter() {
                        override fun finished(input: String?) {
                            Log.trace(TAG, "Rename game object [{}] to [{}].", selectedGO, input)
                            // update sceneGraph
                            selectedGO!!.name = input
                            // update Outline
                            //goNode.name.setText(input + " [" + selectedGO.id + "]");
                            goNode.nameLabel.setText(input)

                            Mundus.postEvent(SceneGraphChangedEvent())
                        }
                    })
            // set position of dialog to menuItem position
            val nodePosX = node.actor.x
            val nodePosY = node.actor.y
            renameDialog.setPosition(nodePosX, nodePosY)
        }
    }

    companion object {

        private val TITLE = "Outline"
        private val TAG = Outline::class.java.simpleName
    }
}
