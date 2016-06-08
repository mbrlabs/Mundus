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

package com.mbrlabs.mundus.ui.modules.sidebar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
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
import com.mbrlabs.mundus.utils.TerrainUtils;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class OutlineTab extends Tab implements
        ProjectChangedEvent.ProjectChangedListener,
        SceneChangedEvent.SceneChangedListener,
        SceneGraphChangedEvent.SceneGraphChangedListener,
        GameObjectSelectedEvent.GameObjectSelectedListener {

    private static final String TITLE = "Outline";

    private VisTable content;
    private VisTree tree;
    private ScrollPane scrollPane;

    private DragAndDrop dragAndDrop;

    private RightClickMenu rightClickMenu;

    @Inject
    private Shaders shaders;
    @Inject
    private ToolManager toolManager;
    @Inject
    private ProjectManager projectManager;

    public OutlineTab() {
        super(false, false);
        Mundus.inject(this);
        Mundus.registerEventListener(this);

        rightClickMenu = new RightClickMenu();

        content = new VisTable();
        content.align(Align.left | Align.top);

        tree = new VisTree();
        tree.getSelection().setProgrammaticChangeEvents(false);
        scrollPane = new VisScrollPane(tree);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        content.add(scrollPane).fill().expand();


        setupDragAndDrop();
        setupListeners();
    }

    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        buildTree(projectManager.current().currScene.sceneGraph);
    }

    @Override
    public void onSceneChanged(SceneChangedEvent sceneChangedEvent) {
        buildTree(projectManager.current().currScene.sceneGraph);
    }

    @Override
    public void onSceneGraphChanged(SceneGraphChangedEvent sceneGraphChangedEvent) {
        buildTree(projectManager.current().currScene.sceneGraph);
    }

    private void setupDragAndDrop() {
        dragAndDrop = new DragAndDrop();

        // source
        dragAndDrop.addSource(new DragAndDrop.Source(tree) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                Tree.Node node = tree.getNodeAt(y);
                if(node != null) {
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
                if (overNode == null && tree.getSelection().isEmpty()) return true;
                if (overNode != null && !tree.getSelection().contains(overNode)) tree.getSelection().set(overNode);
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Tree.Node node = (Tree.Node) payload.getObject();

                final ProjectContext projectContext = projectManager.current();

                if(node != null) {
                    GameObject draggedGo = (GameObject) node.getObject();
                    Tree.Node newParent = tree.getNodeAt(y);

                    // check if a go is dragged in one of its' children or itself
                    if(newParent != null) {
                        GameObject parentGo = (GameObject) newParent.getObject();
                        if(parentGo.isChildOf(draggedGo)) {
                            return;
                        }
                    }

                    // remove child from old parent
                    GameObject oldParent = draggedGo.getParent();
                    if(oldParent == null) {
                        projectContext.currScene.sceneGraph.getGameObjects().removeValue(draggedGo, true);
                    } else {
                        oldParent.getChilds().removeValue(draggedGo, true);
                    }

                    // add to new parent
                    if(newParent == null) {
                        projectContext.currScene.sceneGraph.getGameObjects().add(draggedGo);
                        draggedGo.setParent(null);
                    } else {
                        GameObject parentGo = (GameObject) newParent.getObject();
                        parentGo.addChild(draggedGo);
                        draggedGo.setParent(parentGo);
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
                if (Input.Buttons.RIGHT != button) return;

                Tree.Node node = tree.getNodeAt(y);
                GameObject go = null;
                if(node != null) go = (GameObject) node.getObject();
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
                Selection<Tree.Node> selection =  tree.getSelection();
                if(selection != null && selection.size() > 0) {
                    GameObject go = (GameObject) selection.first().getObject();
                    projectManager.current().currScene.sceneGraph.setSelected(go);
                    toolManager.translateTool.gameObjectSelected(go);
                    Mundus.postEvent(new GameObjectSelectedEvent(go));
                }
            }
        });

    }

    private void buildTree(SceneGraph sceneGraph) {
        tree.clearChildren();

        for(GameObject go : sceneGraph.getGameObjects()) {
            addGameObject(null, go);
        }
    }

    private void addGameObject(Tree.Node treeParentNode, GameObject gameObject) {
        Tree.Node leaf = new Tree.Node(new TreeNode(gameObject));
        leaf.setObject(gameObject);
        if(treeParentNode == null) {
            tree.add(leaf);
        } else {
            treeParentNode.add(leaf);
        }

        if(gameObject.getChilds() != null) {
            for(GameObject goChild : gameObject.getChilds()) {
                addGameObject(leaf, goChild);
            }
        }
    }

    @Override
    public String getTabTitle() {
        return TITLE;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    @Override
    public void onGameObjectSelected(GameObjectSelectedEvent gameObjectSelectedEvent) {
        Tree.Node node = tree.findNode(gameObjectSelectedEvent.getGameObject());

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
            name.setText(go.getName() + " [" + go.getId() + "]");
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

            final ProjectContext projectContext = projectManager.current();
            addEmpty = new MenuItem("Add Empty");
            addTerrain = new MenuItem("Add terrain");
            duplicate = new MenuItem("Duplicate");
            delete = new MenuItem("Delete");

            // add empty
            addEmpty.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    int id = projectContext.obtainID();
                    SceneGraph sg = projectContext.currScene.sceneGraph;
                    if (selectedGO == null) {
                        sg.getGameObjects().add(new GameObject(sg, GameObject.DEFAULT_NAME, id));
                    } else {
                        selectedGO.addChild(new GameObject(sg, GameObject.DEFAULT_NAME, id));
                    }
                    Mundus.postEvent(new SceneGraphChangedEvent());
                }
            });

            // add terrain
            addTerrain.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Terrain terrain = TerrainUtils.createTerrain(projectContext.obtainID(), "Terrain", 1200, 1200, 180);
                    projectContext.terrains.add(terrain);
                    projectContext.currScene.terrainGroup.add(terrain);
                    GameObject terrainGO = TerrainUtils.createTerrainGO(
                            projectContext.currScene.sceneGraph, shaders.terrainShader, projectContext.obtainID(), "Terrain", terrain);
                    projectContext.currScene.sceneGraph.getGameObjects().add(terrainGO);
                    Mundus.postEvent(new SceneGraphChangedEvent());
                }
            });

            // delete game object
            delete.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(selectedGO != null) {
                        if(selectedGO.remove()) {
                            selectedGO = null;
                            Mundus.postEvent(new SceneGraphChangedEvent());
                        }
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

