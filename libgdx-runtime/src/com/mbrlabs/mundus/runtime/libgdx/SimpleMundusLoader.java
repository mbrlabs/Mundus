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

package com.mbrlabs.mundus.runtime.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.exporter.dto.ModelDTO;
import com.mbrlabs.mundus.commons.exporter.dto.ModelInstanceDTO;
import com.mbrlabs.mundus.commons.exporter.dto.ProjectDTO;
import com.mbrlabs.mundus.commons.exporter.dto.SceneDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 27-12-2015
 */
public class SimpleMundusLoader {

    private String folder;
    private ProjectDTO projectDTO;
    private G3dModelLoader modelLoader;

    private Map<Long, Model> models;
    private Array<Scene> scenes;

    public SimpleMundusLoader(String folder) {
        this.folder = folder;
        modelLoader = new G3dModelLoader(new UBJsonReader());
        models = new HashMap<>();
        scenes = new Array<>();
    }

    public Array<Scene> load() {
        Json json = new Json();
        FileHandle mundusFile = Gdx.files.internal(folder + "pro.mundus");
        projectDTO = json.fromJson(ProjectDTO.class, mundusFile);

        // load models
        for(ModelDTO model : projectDTO.getModels()) {
            Model m = modelLoader.loadModel(Gdx.files.internal(getModelPath(model.getId())));
            models.put(model.getId(), m);
        }

        // build scenes
        for(SceneDTO sceneDTO : projectDTO.getScenes()) {
            Scene scene = new Scene();
            scene.name = sceneDTO.getName();
            for(ModelInstanceDTO dto: sceneDTO.getEntities()) {
                ModelInstance mi = new ModelInstance(models.get(dto.getModelID()));
                mi.transform.translate(dto.getPosition()[0], dto.getPosition()[1], dto.getPosition()[2]);
                mi.transform.rotate(dto.getRotation()[0], dto.getRotation()[1], dto.getRotation()[2], 0);
                mi.transform.scl(dto.getScale()[0], dto.getScale()[1], dto.getScale()[2]);
                scene.modelInstances.add(mi);
            }
            scenes.add(scene);
        }

        return scenes;
    }

    public Map<Long, Model> getModels() {
        return models;
    }

    public Array<Scene> getScenes() {
        return scenes;
    }

    private String getModelPath(long id) {
        return folder + "assets/" + id + ".g3db";
    }



}
