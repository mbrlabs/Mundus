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

package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.model.MTexture;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ProjectContext implements Disposable {

    public String absolutePath;
    public String name;

    public Array<String> scenes;
    public Scene currScene;

    public Array<MModel> models;
    public Array<Terrain> terrains;
    public Array<MTexture> textures;

    private long uuidProvider;

    public ProjectContext(long uuidProvider) {
        models = new Array<>();
        textures = new Array<>();
        scenes = new Array<>();
        currScene = new Scene();
        terrains = new Array<>();
        this.uuidProvider = uuidProvider;
    }

    public void copyFrom(ProjectContext other) {
        absolutePath = other.absolutePath;
        name = other.name;
        terrains = other.terrains;
        currScene = other.currScene;
        scenes = other.scenes;
        models = other.models;
        uuidProvider = other.uuidProvider;
    }

    public synchronized long obtainUUID() {
        uuidProvider += 1;
        return uuidProvider;
    }

    public synchronized long getCurrentUUID() {
        return uuidProvider;
    }

    @Override
    public void dispose() {
        for(MModel model : models) {
            model.getModel().dispose();
        }
        models = null;
    }

}
