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

package com.mbrlabs.mundus.runtime.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.importer.*;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.utils.TextureUtils;
import com.mbrlabs.mundus.runtime.libgdx.terrain.Terrain;
import com.mbrlabs.mundus.runtime.libgdx.terrain.TerrainComponent;
import com.mbrlabs.mundus.runtime.libgdx.terrain.TerrainShader;
import com.mbrlabs.mundus.runtime.libgdx.terrain.Utils;

/**
 * @author Marcus Brummer
 * @version 25-01-2016
 */
public class Importer {

    private final String assetsFolder;
    private TerrainShader terrainShader;

    public Importer(String assetsFolder, TerrainShader terrainShader) {
        if(assetsFolder.endsWith("/")) {
            this.assetsFolder = assetsFolder;
        } else {
            this.assetsFolder = assetsFolder + "/";
        }

        this.terrainShader = terrainShader;
    }

    public Project importAll() {
        ProjectDTO dto = parseJson();
        Project project = new Project();

        // textures
        for(TextureDTO texDto : dto.getTextures()) {
            project.getTextures().add(convert(texDto));
        }

        // terrains
        for(TerrainDTO terrainDto : dto.getTerrains()) {
            project.getTerrains().add(new Terrain(assetsFolder, terrainDto, project.getTextures()));
        }

        // scenes
        for(SceneDTO sceneDto : dto.getScenes()) {
            project.getScenes().add(convert(sceneDto, project.getTerrains()));
        }

        return project;
    }

    private ProjectDTO parseJson() {
        Json json = new Json();
        return json.fromJson(ProjectDTO.class, Gdx.files.internal(assetsFolder + "mundus"));
    }

    public MTexture convert(TextureDTO dto) {
        MTexture tex = new MTexture();
        tex.setId(dto.getId());
        tex.texture = TextureUtils.loadMipmapTexture(Gdx.files.internal(assetsFolder + dto.getPath()), true);


        return tex;
    }

    public Scene convert(SceneDTO dto, Array<Terrain> terrains) {
        Scene scene = new Scene();

        scene.setId(dto.getId());
        scene.setName(dto.getName());

        scene.sceneGraph = new SceneGraph(scene);
        scene.sceneGraph.setRoot(convert(dto.getSceneGraph(), scene.sceneGraph, terrains));

        return scene;
    }

    public GameObject convert(GameObjectDTO dto, SceneGraph sceneGraph, Array<Terrain> terrains) {
        final GameObject go = new GameObject(sceneGraph, dto.getName(), dto.getId());
        go.setActive(dto.isActive());

        final float[] trans = dto.getTrans();
        go.transform.translate(trans[0], trans[1], trans[2]);
        go.transform.rotate(trans[3], trans[4], trans[5], 0);
        go.transform.scl(trans[6], trans[7], trans[8]);

        // TODO model component
        if(dto.getTerrC() != null) {
            go.getComponents().add(convert(go, dto.getTerrC(), terrains));
        }

        // recursively convert children
        if(dto.getChilds() != null) {
            for (GameObjectDTO c : dto.getChilds()) {
                go.addChild(convert(c, sceneGraph, terrains));
            }
        }

        return go;
    }

    public TerrainComponent convert(GameObject go, TerrainComponentDTO dto, Array<Terrain> terrains) {
        TerrainComponent terrainComponent = new TerrainComponent(go);
        terrainComponent.setShader(terrainShader);
        terrainComponent.setTerrain(Utils.findTerrainById(terrains, dto.getTerrainID()));

        return terrainComponent;
    }

}
