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
package com.mbrlabs.mundus.ui.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.InputDialog;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.*;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.GameObjectSelectedEvent;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.SceneChangedEvent;
import com.mbrlabs.mundus.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.history.Command;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.history.commands.DeleteCommand;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.utils.TerrainUtils;

/**
 * Outline shows overview about all game objects in the scene
 *
 * @author Marcus Brummer, codenigma
 * @version 27-09-2016
 */
public class Outline extends VisTable implements
        ProjectChangedEvent.ProjectChangedListener,
        SceneChangedEvent.SceneChangedListener,
        SceneGraphChangedEvent.SceneGraphChangedListener,
        GameObjectSelectedEvent.GameObjectSelectedListener {

    private static final String TITLE = "Outline";
    private static final String TAG = Outline.class.getSimpleName();

    private VisTable content;
    private VisTree tree;
    private ScrollPane scrollPane;

    private DragAndDrop dragAndDrop;

    private RightClickMenu rightClickMenu;

    private SceneGraph sceneGraph;

    @Inject
    private Shaders shaders;
    @Inject
    private ToolManager toolManager;
    @Inject
    private ProjectManager projectManager;
    @Inject
    private CommandHistory history;

    private final ProjectContext projectContext;

    public Outline() {
        super();
        Mundus.inject(this);
        Mundus.registerEventListener(this);
        setBackground("window-bg");

        rightClickMenu = new RightClickMenu();

        content = new VisTable();
        content.align(Align.left | Align.top);

        tree = new VisTree();
        tree.getSelection().setProgrammaticChangeEvents(false);
        scrollPane = new VisScrollPane(tree);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        content.add(scrollPane).fill().expand();

        add(new VisLabel(TITLE)).expandX().fillX().pad(3).row();
        addSeparator().row();
        add(content).fill().expand();

        setupDragAndDrop();
        setupListeners();

        sceneGraph = projectManager.current().currScene.sceneGraph;
        projectContext = projectManager.current();
    }

    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        //update to new sceneGraph
        sceneGraph = projectManager.current().currScene.sceneGraph;
        Log.traceTag(TAG, "Project changed. Building scene graph.");
        buildTree(sceneGraph);
    }

    @Override
    public void onSceneChanged(SceneChangedEvent sceneChangedEvent) {
        //update to new sceneGraph
        sceneGraph = projectManager.current().currScene.sceneGraph;
        Log.traceTag(TAG, "Scene changed. Building scene graph.");
        buildTree(sceneGraph);
    }

    @Override
    public void onSceneGraphChanged(SceneGraphChangedEvent sceneGraphChangedEvent) {
        Log.traceTag(TAG, "SceneGraph changed. Building scene graph.");
        buildTree(sceneGraph);
    }

    private void setupDragAndDrop() {
        dragAndDrop = new DragAndDrop();

        // source
        dragAndDrop.addSource(new DragAndDrop.Source(tree) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                Tree.Node node = tree.getNodeAt(y);
                if (node != null) {
                    payload.setObject(node);
                    return payload;
                }

                return null;
            }
        });

        // target
        dragAndDrop.addTarget(new DragAndDrop.Target(tree) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                // Select node under mouse if not over the selection.
                Tree.Node overNode = tree.getNodeAt(y);
                if (overNode == null && tree.getSelection().isEmpty()) {
                    return true;
                }
                if (overNode != null && !tree.getSelection().contains(overNode)) {
                    tree.getSelection().set(overNode);
                }
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Tree.Node node = (Tree.Node) payload.getObject();

                final ProjectContext projectContext = projectManager.current();

                if (node != null) {
                    GameObject draggedGo = (GameObject) node.getObject();
                    Tree.Node newParent = tree.getNodeAt(y);

                    // check if a go is dragged in one of its' children or itself
                    if (newParent != null) {
                        GameObject parentGo = (GameObject) newParent.getObject();
                        if (parentGo.isChildOf(draggedGo)) {
                            return;
                        }
                    }

                    // remove child from old parent
                    draggedGo.remove();

                    // add to new parent
                    if (newParent == null) {
                        projectContext.currScene.sceneGraph.addGameObject(draggedGo);
                    } else {
                        GameObject parentGo = (GameObject) newParent.getObject();
                        parentGo.addChild(draggedGo);
                    }

                    // update tree
                    buildTree(sceneGraph);
                }
            }
        });

    }

    private void setupListeners() {

        scrollPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Ui.getInstance().setScrollFocus(scrollPane);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Ui.getInstance().setScrollFocus(null);
            }

        });

        // right click menu listener
        tree.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Input.Buttons.RIGHT != button) {
                    return;
                }

                Tree.Node node = tree.getNodeAt(y);
                GameObject go = null;
                if (node != null) {
                    go = (GameObject) node.getObject();
                }
                rightClickMenu.show(go, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        // select listener
        tree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Selection<Tree.Node> selection = tree.getSelection();
                if (selection != null && selection.size() > 0) {
                    GameObject go = (GameObject) selection.first().getObject();
                    projectManager.current().currScene.sceneGraph.setSelected(go);
                    toolManager.translateTool.gameObjectSelected(go);
                    Mundus.postEvent(new GameObjectSelectedEvent(go));
                }
            }
        });

    }

    /**
     * Building tree from game objects in sceneGraph, clearing previous
     * sceneGraph
     *
     * @param sceneGraph
     */
    private void buildTree(SceneGraph sceneGraph) {
        tree.clearChildren();

        for (GameObject go : sceneGraph.getGameObjects()) {
            addGoToTree(null, go);
        }
    }

    /**
     * Adding game object to outline
     *
     * @param treeParentNode
     * @param gameObject
     */
    private void addGoToTree(Tree.Node treeParentNode, GameObject gameObject) {
        Tree.Node leaf = new Tree.Node(new TreeNode(gameObject));
        leaf.setObject(gameObject);
        if (treeParentNode == null) {
            tree.add(leaf);
        } else {
            treeParentNode.add(leaf);
        }
        //Always expand after adding new node
        leaf.expandTo();
        if (gameObject.getChildren() != null) {
            for (GameObject goChild : gameObject.getChildren()) {
                addGoToTree(leaf, goChild);
            }
        }
    }

    /**
     * Removing game object from tree and outline
     *
     * @param go
     */
    private void removeGo(GameObject go) {
        //run delete command, updating sceneGraph and outline
        Command deleteCommand = new DeleteCommand(go, tree.findNode(go));
        history.add(deleteCommand);
        deleteCommand.execute(); //run delete
    }

    /**
     * Deep copy of all game objects
     * @param go        the game object for cloning, with children
     * @param parent    game object on which clone will be added
     */
    private void duplicateGO(GameObject go, GameObject parent) {
        Log.traceTag(TAG, "Duplicate [{}] with parent [{}]", go, parent);
        //create duplicate
        GameObject goCopy = new GameObject(go, projectContext.obtainID());
        //add copy to tree
        //outline
        Tree.Node n = tree.findNode(parent);
        addGoToTree(n, goCopy);
        //sceneGraph
        parent.addChild(goCopy);
        //look for children
        if (go.getChildren() != null) {
            for (GameObject child : go.getChildren()) {
                duplicateGO(child, goCopy);
            }
        }
    }

    @Override
    public void onGameObjectSelected(GameObjectSelectedEvent gameObjectSelectedEvent) {
        Tree.Node node = tree.findNode(gameObjectSelectedEvent.getGameObject());
        Log.traceTag(TAG, "Select game object [{}].", node.getObject());
        tree.getSelection().clear();
        tree.getSelection().add(node);
        node.expandTo();
    }

    /**
     * A node of the ui tree hierarchy.
     */
    private class TreeNode extends VisTable {

        private VisLabel name;

        public TreeNode(final GameObject go) {
            super();
            name = new VisLabel();
            add(name).expand().fill();
            name.setText(go.name + " [" + go.id + "]");
        }
    }

    /**
     *
     */
    private class RightClickMenu extends PopupMenu {

        private MenuItem addEmpty;
        private MenuItem addTerrain;
        private MenuItem duplicate;
        private MenuItem rename;
        private MenuItem delete;

        private GameObject selectedGO;

        public RightClickMenu() {
            super();

            addEmpty = new MenuItem("Add Empty");
            addTerrain = new MenuItem("Add terrain");
            duplicate = new MenuItem("Duplicate");
            rename = new MenuItem("Rename");
            delete = new MenuItem("Delete");

            // add empty
            addEmpty.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    int id = projectContext.obtainID();
                    //the new game object
                    GameObject go = new GameObject(sceneGraph, GameObject.DEFAULT_NAME, id);
                    //update outline
                    if (selectedGO == null) {
                        //update sceneGraph
                        Log.traceTag(TAG, "Add empty game object [{}] in root node.", go);
                        sceneGraph.addGameObject(go);
                        //update outline
                        addGoToTree(null, go);
                    } else {
                        Log.traceTag(TAG, "Add empty game object [{}] child in node [{}].", go, selectedGO);
                        //update sceneGraph
                        selectedGO.addChild(go);
                        //update outline
                        Tree.Node n = tree.findNode(selectedGO);
                        addGoToTree(n, go);
                    }
                    Mundus.postEvent(new SceneGraphChangedEvent());
                }
            });

            // add terrain
            addTerrain.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //To-DO: Terrain config popup: set width and height values, maybe heightmap import options
                    Log.traceTag(TAG, "Add terrain game object in root node.");
                    Terrain terrain = TerrainUtils.createTerrain(projectContext.obtainID(), "Terrain", 1200, 1200, 180);
                    projectContext.terrains.add(terrain);
                    //projectContext.currScene.terrainGroup.add(terrain);
                    GameObject terrainGO = TerrainUtils.createTerrainGO(
                            sceneGraph, shaders.terrainShader, projectContext.obtainID(), "Terrain", terrain);
                    //update sceneGraph
                    sceneGraph.addGameObject(terrainGO);
                    //update outline
                    addGoToTree(null, terrainGO);

                    Mundus.postEvent(new SceneGraphChangedEvent());
                }
            });

            rename.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedGO != null) {
                        showRenameDialog();
                    }
                }
            });

            // duplicate node
            duplicate.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedGO != null && !duplicate.isDisabled()) {
                        duplicateGO(selectedGO, selectedGO.getParent());
                        Mundus.postEvent(new SceneGraphChangedEvent());
                    }
                }
            });
            
            // delete game object
            delete.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedGO != null) {
                        removeGo(selectedGO);
                        Mundus.postEvent(new SceneGraphChangedEvent());
                    }
                }
            });

            addItem(addEmpty);
            addItem(addTerrain);
            addItem(rename);
            addItem(duplicate);
            addItem(delete);
        }

        /**
         * Right click event opens menu and enables more options if selected
         * game object is active.
         *
         * @param go
         * @param x
         * @param y
         */
        public void show(GameObject go, float x, float y) {
            selectedGO = go;
            showMenu(Ui.getInstance(), x, y);

            //check if game oject is selected
            if (selectedGO != null) {
                //Activate menu options for selected game objects
                rename.setDisabled(false);
                delete.setDisabled(false);
            } else {
                //disable MenuItems which only work with selected Item
                rename.setDisabled(true);
                delete.setDisabled(true);
            }
            
            //terrain can not be duplicated
            if (selectedGO == null || selectedGO.findComponentByType(Component.Type.TERRAIN) != null) {
                duplicate.setDisabled(true);
            } else {
                duplicate.setDisabled(false);
            }
        }

        public void showRenameDialog() {
            final Tree.Node node = tree.findNode(selectedGO);
            final TreeNode goNode = (TreeNode) node.getActor();

            InputDialog renameDialog = Dialogs.showInputDialog(Ui.getInstance(), "Rename", "", new InputDialogAdapter() {
                @Override
                public void finished(String input) {
                    Log.traceTag(TAG, "Rename game object [{}] to [{}].", selectedGO, input);
                    //update sceneGraph           
                    selectedGO.name = input;
                    //update Outline
                    goNode.name.setText(input + " [" + selectedGO.id + "]");

                    Mundus.postEvent(new SceneGraphChangedEvent());
                }
            });
            //set position of dialog to menuItem position
            float nodePosX = node.getActor().getX();
            float nodePosY = node.getActor().getY();
            renameDialog.setPosition(nodePosX, nodePosY);
        }
    }
}
