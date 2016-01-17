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

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTree;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.scene3d.SceneGraph;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.Subscribe;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class OutlineTab extends Tab {

    private static final String TITLE = "Outline";

    private VisTable content;
    private Tree tree;
    private ScrollPane scrollPane;

    @Inject
    private ProjectContext projectContext;
    @Inject
    private EventBus eventBus;

    public OutlineTab() {
        super(false, false);
        Mundus.inject(this);
        eventBus.register(this);

        content = new VisTable();
        content.align(Align.left | Align.top);

        tree = new VisTree();
        scrollPane = new ScrollPane(tree);
        scrollPane.setSmoothScrolling(true);

        content.add(scrollPane).fill().expand();
    }

    @Subscribe
    public void reloadAllModels(ProjectChangedEvent projectChangedEvent) {
        buildTree(projectContext.currScene.sceneGraph);
    }

    private void buildTree(SceneGraph sceneGraph) {
        tree.clearChildren();

        GameObject rootGo = sceneGraph.getRoot();

        Tree.Node treeRoot = new Tree.Node(new TreeNode(rootGo));
        tree.add(treeRoot);

        for(GameObject goChild : rootGo.getChilds()) {
            addGameObject(treeRoot, goChild);
        }

        treeRoot.setExpanded(true);
    }

    private void addGameObject(Tree.Node treeParentNode, GameObject gameObject) {
        System.out.println("Adding gameObject: " + gameObject.getName());

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
    private class TreeNode extends Table {

        private VisLabel name;

        private GameObject go;

        public TreeNode() {
            super();
            name = new VisLabel();
            add(name).left().top().expandX().fillX();
        }

        public TreeNode(GameObject go) {
            this();
            setGameObject(go);
        }

        public void setGameObject(GameObject go) {
            this.go = go;
            name.setText(go.getName());
        }

    }

}

