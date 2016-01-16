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

package com.mbrlabs.mundus.commons.scene3d;

import com.badlogic.gdx.Gdx;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class SceneGraph {

    protected GameObject root;

    public SceneGraph() {
        root = new GameObject();
    }

    public GameObject getRoot() {
        return this.root;
    }

    public void render() {
        root.render(Gdx.graphics.getDeltaTime());
    }

    public void render(float delta) {
        root.render(delta);
    }

    public void update() {
        root.update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {
        root.update(delta);
    }

}
