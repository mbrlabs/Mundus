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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.exporter.dto.*;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainInstance;
import com.mbrlabs.mundus.commons.utils.TextureUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author Marcus Brummer
 * @version 27-12-2015
 */
public class SimpleMundusLoader {

    private String folder;
    private ProjectDTO projectDTO;
    private G3dModelLoader modelLoader;

    private Map<Long, Model> models;
    private Map<Long, Terrain> terrains;
    private Array<Scene> scenes;

    public SimpleMundusLoader(String folder) {
        this.folder = folder;
        modelLoader = new G3dModelLoader(new UBJsonReader());
        terrains = new HashMap<>();
        models = new HashMap<>();
        scenes = new Array<>();
    }

    public Array<Scene> load() {
        Json json = new Json();
        FileHandle mundusFile = findProjectDescriptor(folder);
        projectDTO = json.fromJson(ProjectDTO.class, mundusFile);

        // load models
        for(ModelDTO model : projectDTO.getModels()) {
            Model m = modelLoader.loadModel(Gdx.files.internal(getModelPath(model.getId())));
            models.put(model.getId(), m);
        }

        // load Terrains
        for(TerrainDTO dto : projectDTO.getTerrains()) {
            terrains.put(dto.getId(), loadTerrain(dto));
        }

        // build scenes
        for(SceneDTO sceneDTO : projectDTO.getScenes()) {
            Scene scene = new Scene();
            scene.name = sceneDTO.getName();
            FogDTO fogDTO = sceneDTO.getFog();
            if(fogDTO != null) {
                Fog fog = new Fog();
                fog.density = fogDTO.getDensity();
                fog.gradient = fogDTO.getGradient();
                fog.color = new Color(fogDTO.getColor());
                scene.environment.setFog(fog);
            }

            for(ModelInstanceDTO dto: sceneDTO.getEntities()) {
                ModelInstance mi = new ModelInstance(models.get(dto.getModelID()));
                mi.transform.translate(dto.getPosition()[0], dto.getPosition()[1], dto.getPosition()[2]);
                mi.transform.rotate(dto.getRotation()[0], dto.getRotation()[1], dto.getRotation()[2], 0);
                mi.transform.scl(dto.getScale()[0], dto.getScale()[1], dto.getScale()[2]);
                scene.modelInstances.add(mi);
            }
            for(TerrainInstanceDTO dto : sceneDTO.getTerrains()) {
                TerrainInstance terrainInstance = new TerrainInstance(terrains.get(dto.getTerrainID()));
                scene.terrainGroup.add(terrainInstance);
                terrainInstance.transform.translate(dto.getPosition()[0], dto.getPosition()[1], dto.getPosition()[2]);
            }

            scenes.add(scene);
        }

        return scenes;
    }

    private FileHandle findProjectDescriptor(String folder) {
        for(FileHandle f : Gdx.files.internal(folder).list("mundus")) {
            if(!f.isDirectory()) {
                return f;
            }
        }

        return null;
    }

    private Terrain loadTerrain(TerrainDTO dto) {
        FloatArray floatArray = new FloatArray();

        Terrain terrain = new Terrain(dto.getVertexResolution());
        try(DataInputStream is = new DataInputStream(
                new BufferedInputStream(new GZIPInputStream(new FileInputStream(getTerrainPath(dto.getId())))))) {
            while (is.available() > 0) {
                floatArray.add(is.readFloat());
            }
        } catch (EOFException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        terrain.init();
        terrain.heightData = floatArray.toArray();
        terrain.update();
        Texture tex = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/stone_hr.jpg"));
        terrain.setTexture(tex);

        return terrain;
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

    private String getTerrainPath(long id) {
        return folder + "assets/" + id + ".terra";
    }

}
