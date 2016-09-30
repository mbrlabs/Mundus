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
package com.mbrlabs.mundus.history.commands;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.history.Command;
import com.mbrlabs.mundus.utils.Log;

/**
 * Delete command for game objects 
 * Deletion will update sceneGraph and outline
 * 
 * @author codenigma
 * @version 28-09-2016
 */
public class DeleteCommand implements Command {

    private static final String TAG = DeleteCommand.class.getSimpleName();

    private GameObject go;
    private GameObject parentGO;
    private Tree.Node node;
    private Tree.Node parentNode;
    private Tree tree;

    public DeleteCommand(GameObject go, Tree.Node node) {
        this.go = go;
        this.parentGO = go.getParent();
        this.node = node;
        this.parentNode = node.getParent();
        this.tree = node.getTree();
    }

    @Override
    public void execute() {
        Log.traceTag(TAG, "Remove game object [{}]", go);
        //remove go from sceneGraph
        go.remove();
        //remove from outline tree
        tree.remove(node);
        Mundus.postEvent(new SceneGraphChangedEvent());
    }

    @Override
    public void undo() {
        Log.traceTag(TAG, "Undo remove of game object [{}]", go);
        //add to sceneGraph
        parentGO.addChild(go);
        //add to outline
        if (parentNode == null)
            tree.add(node);
        else
            parentNode.add(node);     
        node.expandTo();
        Mundus.postEvent(new SceneGraphChangedEvent());
    }

    @Override
    public void dispose() {
        go = null;
        parentGO = null;
        parentNode = null;
        node = null;
        tree = null;
    }

}
