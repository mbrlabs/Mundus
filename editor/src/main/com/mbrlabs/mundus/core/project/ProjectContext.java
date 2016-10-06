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

package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.core.EditorScene;
import com.mbrlabs.mundus.utils.Log;

/**
 * A project context represents an loaded and opened project.
 *
 * A project context can have many scenes, nut only one scene at a time can be
 * active.
 *
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ProjectContext implements Disposable {

    private static final String TAG = ProjectContext.class.getSimpleName();

    public String path;
    public String name;

    public Array<String> scenes;
    public EditorScene currScene;

    public Array<MModel> models;
    public Array<Terrain> terrains;
    public Array<MTexture> textures;

    private int idProvider;

    /** set by kryo when project is loaded. do not use this */
    public String activeSceneName;

    public ProjectContext(int idProvider) {
        models = new Array<>();
        textures = new Array<>();
        scenes = new Array<>();
        currScene = new EditorScene();
        terrains = new Array<>();
        this.idProvider = idProvider;
    }

    public void copyFrom(ProjectContext other) {
        path = other.path;
        name = other.name;
        terrains = other.terrains;
        currScene = other.currScene;
        scenes = other.scenes;
        models = other.models;
        idProvider = other.idProvider;
        textures = other.textures;
    }

    public synchronized int obtainID() {
        idProvider += 1;
        return idProvider;
    }

    public synchronized int inspectCurrentID() {
        return idProvider;
    }

    @Override
    public void dispose() {
        Log.debug(TAG, "Disposing project current {}", path);
        for (MModel model : models) {
            model.getModel().dispose();
        }
        models = null;
    }

}
