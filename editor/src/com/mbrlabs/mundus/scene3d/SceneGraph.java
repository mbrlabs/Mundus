/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.scene3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.core.Scene;

import java.util.Iterator;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class SceneGraph implements Iterable<GameObject> {

    protected GameObject root;

    public Scene scene;
    public ModelBatch batch;

    private GameObject selected;

    public SceneGraph(Scene scene) {
        root = new GameObject(this);
        this.scene = scene;
    }

    public ModelBatch getBatch() {
        return batch;
    }

    public void setBatch(ModelBatch batch) {
        this.batch = batch;
    }

    public GameObject getRoot() {
        return this.root;
    }

    public void setRoot(GameObject root) {
        this.root = root;
    }

    public void render() {
        batch.begin(scene.cam);
        root.render(Gdx.graphics.getDeltaTime());
        batch.end();
    }

    public void render(float delta) {
        batch.begin(scene.cam);
        root.render(delta);
        batch.end();
    }

    public void update() {
        root.update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {
        root.update(delta);
    }

    public GameObject getSelected() {
        return selected;
    }

    public void setSelected(GameObject selected) {
        this.selected = selected;
    }

    public Array<GameObject> getTerrainGOs(Array<GameObject> out) {
        out.clear();
        if(root.getChilds() != null) {
            for(GameObject c : root.getChilds()) {
                if(c.getComponentByType(Component.Type.TERRAIN) != null) {
                    out.add(c);
                }
            }
        }

        return out;
    }

    // TODO ================================================================
    // TODO IMPLEMENT NON RECURSIVE ITERATOR
    // TODO ================================================================

    @Override
    @Deprecated
    public Iterator<GameObject> iterator() {
        return new SceneGraphIterator(this);
    }

    /**
     * Iterates over the game objects of the scene graph.
     */
    private class SceneGraphIterator implements Iterator<GameObject> {

        private SceneGraph sceneGraph;

        public SceneGraphIterator(SceneGraph sceneGraph) {
            this.sceneGraph = sceneGraph;
        }

        @Override
        public boolean hasNext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public GameObject next() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
