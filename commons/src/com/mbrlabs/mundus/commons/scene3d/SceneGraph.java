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

package com.mbrlabs.mundus.commons.scene3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.Scene;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class SceneGraph {

    protected Array<GameObject> gameObjects;

    public Scene scene;
    public ModelBatch batch;

    private GameObject selected;

    public SceneGraph(Scene scene) {
        gameObjects = new Array<GameObject>();
        this.scene = scene;
    }

    public ModelBatch getBatch() {
        return batch;
    }

    public void setBatch(ModelBatch batch) {
        this.batch = batch;
    }

    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    public void render(float delta) {
        batch.begin(scene.cam);
        for(GameObject go : gameObjects) {
            go.render(delta);
        }
        batch.end();
    }

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {
        for(GameObject go : gameObjects) {
            go.update(delta);
        }
    }

    public Array<GameObject> getGameObjects() {
        return gameObjects;
    }

    public GameObject getSelected() {
        return selected;
    }

    public void setSelected(GameObject selected) {
        this.selected = selected;
    }

}
