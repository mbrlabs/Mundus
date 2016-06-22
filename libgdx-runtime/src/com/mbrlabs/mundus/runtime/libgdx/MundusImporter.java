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

package com.mbrlabs.mundus.runtime.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.importer.*;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.terrain.SplatMap;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainTexture;
import com.mbrlabs.mundus.commons.utils.TextureUtils;

/**
 * @author Marcus Brummer
 * @version 25-01-2016
 */
public class MundusImporter {

    private Shader terrainShader;
    private Shader entityShader;

    private final String assetsFolder;
    private G3dModelLoader g3dModelLoader;

    public MundusImporter(String assetsFolder, Shader terrainShader, Shader entityShader) {
        if(assetsFolder.endsWith("/")) {
            this.assetsFolder = assetsFolder;
        } else {
            this.assetsFolder = assetsFolder + "/";
        }

        this.terrainShader = terrainShader;
        this.entityShader = entityShader;
        this.g3dModelLoader = new G3dModelLoader(new UBJsonReader());
    }

    public Project importAll() {
        ProjectDTO dto = parseJson();
        Project project = new Project();

        // textures
        for(TextureDTO texDto : dto.getTextures()) {
            project.getTextures().add(convert(texDto));
        }

        // terrains
        for(TerrainDTO ter : dto.getTerrains()) {
            // textures
            final TerrainTexture texture = new TerrainTexture();

            MTexture tex = Utils.findTextureById(project.getTextures(), ter.getTexBase());
            texture.setSplatTexture(new SplatTexture(SplatTexture.Channel.BASE, tex));

            tex = Utils.findTextureById(project.getTextures(), ter.getTexR());
            if(tex != null) texture.setSplatTexture(new SplatTexture(SplatTexture.Channel.R, tex));

            tex = Utils.findTextureById(project.getTextures(), ter.getTexG());
            if(tex != null) texture.setSplatTexture(new SplatTexture(SplatTexture.Channel.G, tex));

            tex = Utils.findTextureById(project.getTextures(), ter.getTexB());
            if(tex != null) texture.setSplatTexture(new SplatTexture(SplatTexture.Channel.B, tex));

            tex = Utils.findTextureById(project.getTextures(), ter.getTexA());
            if(tex != null) texture.setSplatTexture(new SplatTexture(SplatTexture.Channel.A, tex));


            if(ter.getSplatmapPath() != null) {
                SplatMap sm = new SplatMap(new Pixmap(Gdx.files.internal(assetsFolder + ter.getSplatmapPath())));
                texture.setSplatmap(sm);
            }

            final float[] heightData = TerraLoader.readTerraFile(Gdx.files.internal(assetsFolder + ter.getTerraPath()));
            final Terrain t = new Terrain(ter.getVertexRes(), ter.getWidth(), ter.getDepth(), heightData, texture);
            t.id = ter.getId();
            t.init();
            project.getTerrains().add(t);
        }

        // models
        for(ModelDTO modelDTO : dto.getModels()) {
            project.getModels().add(convert(modelDTO));
        }

        // scenes
        for(SceneDTO sceneDto : dto.getScenes()) {
            project.getScenes().add(convert(sceneDto, project.getTerrains(), project.getModels()));
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

    public MModel convert(ModelDTO dto) {
        MModel mod = new MModel();
        mod.setModel(g3dModelLoader.loadModel(Gdx.files.internal(assetsFolder + dto.getG3db())));
        mod.id = dto.getId();

        return mod;
    }

    public Scene convert(SceneDTO dto, Array<Terrain> terrains, Array<MModel> models) {
        Scene scene = new Scene();

        scene.setId(dto.getId());
        scene.setName(dto.getName());

        scene.sceneGraph = new SceneGraph(scene);
        for(GameObjectDTO go : dto.getSceneGraph()) {
            scene.sceneGraph.getGameObjects().add(convert(go, scene.sceneGraph, terrains, models));
        }

        return scene;
    }

    public GameObject convert(GameObjectDTO dto, SceneGraph sceneGraph, Array<Terrain> terrains, Array<MModel> models) {
        final GameObject go = new GameObject(sceneGraph, dto.getName(), (int)dto.getId());
        go.active = dto.isActive();

        final float[] trans = dto.getTrans();
        go.translate(trans[0], trans[1], trans[2]);
        // FIXME
        // go.rotate(trans[3], trans[4], trans[5]);
        go.scale(trans[6], trans[7], trans[8]);

        if(dto.getTerrC() != null) {
            go.getComponents().add(convert(go, dto.getTerrC(), terrains));
        }
        if(dto.getModelC() != null) {
            go.getComponents().add(convert(go, dto.getModelC(), models));
        }

        // recursively convert children
        if(dto.getChilds() != null) {
            for (GameObjectDTO c : dto.getChilds()) {
                go.addChild(convert(c, sceneGraph, terrains, models));
            }
        }

        return go;
    }

    public TerrainComponent convert(GameObject go, TerrainComponentDTO dto, Array<Terrain> terrains) {
        TerrainComponent terrainComponent = new TerrainComponent(go);
        terrainComponent.setShader(terrainShader);

        Terrain terrain = Utils.findTerrainById(terrains, dto.getTerrainID());
        System.out.println(dto.getTerrainID());
        terrain.transform.set(go.getTransform());
        terrainComponent.setTerrain(terrain);

        return terrainComponent;
    }

    public ModelComponent convert(GameObject go, ModelComponentDTO dto, Array<MModel> models) {
        ModelComponent modelComponent = new ModelComponent(go);
        // TODO save models in map to remove the search every time

        ModelInstance mi = new ModelInstance(Utils.findModelById(models, dto.getModelID()));
        mi.transform.set(go.getTransform());

        modelComponent.setModelInstance(mi);
        modelComponent.setShader(entityShader);
        return modelComponent;
    }

}
