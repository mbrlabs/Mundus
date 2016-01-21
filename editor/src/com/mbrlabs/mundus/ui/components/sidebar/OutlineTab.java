/*
 * Copyright (c) 2015. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.events.SceneGraphModified;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.scene3d.SceneGraph;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.Subscribe;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class OutlineTab extends Tab {

    private static final String TITLE = "Outline";

    private VisTable content;
    private VisTree tree;
    private ScrollPane scrollPane;

    private RightClickMenu rightClickMenu;

    @Inject
    private ProjectContext projectContext;

    public OutlineTab() {
        super(false, false);
        Mundus.inject(this);
        Mundus.registerEventListener(this);

        rightClickMenu = new RightClickMenu();

        content = new VisTable();
        content.align(Align.left | Align.top);

        tree = new VisTree();
        scrollPane = new ScrollPane(tree);
        scrollPane.setFlickScroll(false);

        content.add(scrollPane).fill().expand();

        setupListeners();
    }

    @Subscribe
    public void reloadAllModels(ProjectChangedEvent projectChangedEvent) {
        buildTree(projectContext.currScene.sceneGraph);
    }

    @Subscribe
    public void newModelAdded(SceneGraphModified sceneGraphModified) {
        buildTree(projectContext.currScene.sceneGraph);
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
                if (node == null) return;

                GameObject go = ((TreeNode) node.getActor()).go;
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
                    GameObject go = ((TreeNode)selection.first().getActor()).go;
                    projectContext.currScene.sceneGraph.setSelected(go);
                    System.out.println("Outline selected: " + projectContext.currScene.sceneGraph.getSelected().getName());
                }
            }
        });

    }

    private void buildTree(SceneGraph sceneGraph) {
        tree.clearChildren();

        GameObject rootGo = sceneGraph.getRoot();
        VisTree.Node treeRoot = new VisTree.Node(new TreeNode(rootGo));
        tree.add(treeRoot);

        if(rootGo.getChilds() != null) {
            for(GameObject goChild : rootGo.getChilds()) {
                addGameObject(treeRoot, goChild);
            }
        }

        treeRoot.setExpanded(true);
    }

    private void addGameObject(Tree.Node treeParentNode, GameObject gameObject) {
        Tree.Node leaf = new Tree.Node(new TreeNode(gameObject));
        treeParentNode.add(leaf);

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

    /**
     * A node of the ui tree hierarchy.
     */
    private class TreeNode extends VisTable {

        private VisLabel name;

        private GameObject go;

        public TreeNode(final GameObject go) {
            super();
            this.go = go;

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
        private MenuItem rename;
        private MenuItem duplicate;
        private MenuItem delete;


        private GameObject selectedGO;

        public RightClickMenu() {
            super();

            addEmpty = new MenuItem("Add Empty");
            addTerrain = new MenuItem("Add terrain");
            rename = new MenuItem("Rename");
            duplicate = new MenuItem("Duplicate");
            delete = new MenuItem("Delete");

            // add empty
            addEmpty.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedGO != null) {
                        long id = projectContext.obtainUUID();
                        selectedGO.addChild(new GameObject(selectedGO.sceneGraph, GameObject.DEFAULT_NAME, id));
                        Mundus.postEvent(new SceneGraphModified());
                    }
                }
            });

            // add terrain
            addTerrain.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(selectedGO.getParent() != null) {
                        DialogUtils.showErrorDialog(Ui.getInstance(), "Terrains must be direct children of the root game object.");
                    }
                }
            });

            // delete game object
            delete.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(selectedGO != null) {
                        if(selectedGO.remove()) {
                            selectedGO = null;
                            Mundus.postEvent(new SceneGraphModified());
                        }
                    }
                }
            });

            addItem(addEmpty);
            addItem(addTerrain);
            addItem(rename);
            addItem(duplicate);
            addItem(delete);
        }

        public void show(GameObject go, float x, float y) {
            selectedGO = go;
            showMenu(Ui.getInstance(), x, y);
        }

    }

}

