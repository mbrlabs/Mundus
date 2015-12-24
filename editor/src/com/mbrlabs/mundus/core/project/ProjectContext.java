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

import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.terrain.Terrain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ProjectContext implements Disposable {

    public String path;
    public String name;
    public String id;

    public List<Scene> scenes;
    public Scene currScene;

    public List<MModel> models;
    public List<Terrain> terrains;

    private long uuidProvider;
    public boolean loaded = false;

    public ProjectContext(long uuidProvider) {
        models = new ArrayList<>();
        scenes = new ArrayList<>();
        currScene = new Scene();
        terrains = new ArrayList<>();
        this.uuidProvider = uuidProvider;

    }

    public void copyFrom(ProjectContext other) {
        path = other.path;
        name = other.name;
        terrains = other.terrains;
        currScene = other.currScene;
        scenes = other.scenes;
        id = other.id;
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
