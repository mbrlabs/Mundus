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
import com.mbrlabs.mundus.core.Scene;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class SceneGraph {

    protected GameObject root;

    public Scene scene;
    public ModelBatch batch;

    private GameObject selected;

    public SceneGraph(Scene scene, ModelBatch modelBatch) {
        root = new GameObject(this);
        this.batch = modelBatch;
        this.scene = scene;
    }

    public GameObject getRoot() {
        return this.root;
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

}
