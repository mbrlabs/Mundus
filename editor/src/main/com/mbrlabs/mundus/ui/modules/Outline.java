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
import com.kotcrab.vis.ui.widget.*;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.GameObjectSelectedEvent;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.SceneChangedEvent;
import com.mbrlabs.mundus.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.utils.TerrainUtils;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
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

        add(new VisLabel("Outline")).expandX().fillX().pad(3).row();
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
                    buildTree(projectContext.currScene.sceneGraph);
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
     * Building tree from game objects in sceneGraph
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
        //remove from outline
        Tree.Node n = tree.findNode(go);
        tree.remove(n);
        //remove from sceneGraph
        go.remove();
        go = null;
    }

//    private void duplicateGO(GameObject go) {
//        int id = projectContext.obtainID();
//
//        GameObject parentGameObject = go.getParent();
//        //the game object for duplication, usage of copy constructor
//        GameObject duplicateGameObject = new GameObject(go, id);
//        //update sceneGraph
//        Log.traceTag("Outline", "Duplicate game object [{}] in parent layer [{}].", go, parentGameObject);
//        parentGameObject.addChild(duplicateGameObject);
//        //update outline
//        Tree.Node n = tree.findNode(parentGameObject);
//        addGoToTree(n, duplicateGameObject);
//        //children of go, duplicate recursiv  ---> NOT WORKING YET, LOOP :(
//        if (go.getChildren() != null) {
//            for (GameObject goChild : go.getChildren()) {
//                
//                duplicateGO(goChild);
//            }
//        }
//    }

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
        private MenuItem delete;

        private GameObject selectedGO;

        public RightClickMenu() {
            super();

            addEmpty = new MenuItem("Add Empty");
            addTerrain = new MenuItem("Add terrain");
            duplicate = new MenuItem("Duplicate");
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

            // duplicate node, (How to solve copy/clone of game objects?)
//            duplicate.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    duplicateGO(selectedGO);
//
//                    Mundus.postEvent(new SceneGraphChangedEvent());
//                }
//            });

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
            addItem(duplicate);
            addItem(delete);
        }

        public void show(GameObject go, float x, float y) {
            selectedGO = go;
            showMenu(Ui.getInstance(), x, y);
        }

    }
}
