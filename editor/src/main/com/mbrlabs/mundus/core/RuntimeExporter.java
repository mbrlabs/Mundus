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

package com.mbrlabs.mundus.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.importer.*;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.core.kryo.DescriptorConverter;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.terrain.SplatTexture;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.terrain.TerrainTexture;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class RuntimeExporter {

    private static final Vector3 vec3 = new Vector3();
    private static final Quaternion quat = new Quaternion();

    public static void export(KryoManager kryoManager,
                              ProjectContext projectContext,
                              FileHandle destFolder,
                              boolean prettyPrint) throws IOException {
        ProjectDTO dto = new ProjectDTO();
        dto.setName(projectContext.name);

        // models
        ModelDTO[] models = new ModelDTO[projectContext.models.size];
        for(int i = 0; i < models.length; i++) {
            models[i] = convert(projectContext.models.get(i));
        }
        dto.setModels(models);

        // terrains
        TerrainDTO[] terrains = new TerrainDTO[projectContext.terrains.size];
        for(int i = 0; i < terrains.length; i++) {
            terrains[i] = convert(projectContext.terrains.get(i));
        }
        dto.setTerrains(terrains);

        // textures
        TextureDTO[] textures = new TextureDTO[projectContext.textures.size];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = convert(projectContext.textures.get(i));
        }
        dto.setTextures(textures);

        // scenes
        SceneDTO[] scenes = new SceneDTO[projectContext.scenes.size];
        for(int i = 0; i < scenes.length; i++) {
            String name = projectContext.scenes.get(i);
            Scene scene = DescriptorConverter.convert(kryoManager.loadScene(projectContext, name),
                    projectContext.terrains, projectContext.models);
            scenes[i] = convert(scene);
        }
        dto.setScenes(scenes);

        // write JSON
        if(!destFolder.exists()) {
            destFolder.mkdirs();
        }
        FileHandle jsonOutput = Gdx.files.absolute(FilenameUtils.concat(destFolder.path(), "mundus"));
        OutputStream outputStream = new FileOutputStream(jsonOutput.path());
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        String output = prettyPrint ? json.prettyPrint(dto) : json.toJson(dto);
        IOUtils.write(output, outputStream);

        // copy assets
        FileHandle assetOutput = Gdx.files.absolute(FilenameUtils.concat(destFolder.path(), "assets"));
        Gdx.files.absolute(FilenameUtils.concat(projectContext.absolutePath, "assets")).copyTo(assetOutput);
    }

    public static TerrainDTO convert(Terrain terrain) {
        TerrainDTO dto = new TerrainDTO();
        dto.setId(terrain.id);
        dto.setName(terrain.name);
        dto.setDepth(terrain.terrainDepth);
        dto.setWidth(terrain.terrainWidth);
        dto.setVertexRes(terrain.vertexResolution);
        dto.setTerraPath(terrain.terraPath);

        TerrainTexture tex = terrain.getTerrainTexture();
        if(tex.getSplatmap() != null) dto.setSplatmapPath(tex.getSplatmap().getPath());
        // Base
        if(tex.getTexture(SplatTexture.Channel.BASE) != null) {
            dto.setTexBase(tex.getTexture(SplatTexture.Channel.BASE).texture.getId());
        }
        // R
        if(tex.getTexture(SplatTexture.Channel.R) != null) {
            dto.setTexR(tex.getTexture(SplatTexture.Channel.R).texture.getId());
        }
        // G
        if(tex.getTexture(SplatTexture.Channel.G) != null) {
            dto.setTexG(tex.getTexture(SplatTexture.Channel.G).texture.getId());
        }
        // B
        if(tex.getTexture(SplatTexture.Channel.B) != null) {
            dto.setTexB(tex.getTexture(SplatTexture.Channel.B).texture.getId());
        }
        // A
        if(tex.getTexture(SplatTexture.Channel.A) != null) {
            dto.setTexA(tex.getTexture(SplatTexture.Channel.A).texture.getId());
        }

        return dto;
    }

    public static TerrainComponentDTO convert(TerrainComponent terrainComponent) {
        TerrainComponentDTO dto = new TerrainComponentDTO();
        dto.setTerrainID(terrainComponent.getTerrain().id);

        return dto;
    }

    public static ModelComponentDTO convert(ModelComponent modelComponent) {
        ModelComponentDTO dto = new ModelComponentDTO();
        dto.setModelID(modelComponent.getModelInstance().getModel().id);

        return dto;
    }

    public static SceneDTO convert(Scene scene) {
        SceneDTO dto = new SceneDTO();
        dto.setId(scene.getId());
        dto.setName(scene.getName());
        dto.setSceneGraph(convert(scene.sceneGraph.getRoot()));

        return dto;
    }

    public static ModelDTO convert(MModel model) {
        ModelDTO dto = new ModelDTO();
        dto.setId(model.id);
        dto.setName(model.name);
        dto.setG3db(model.g3dbPath);
        dto.setTex(model.texturePath);

        return dto;
    }

    public static TextureDTO convert(MTexture texture) {
        TextureDTO dto = new TextureDTO();
        dto.setId(texture.getId());
        dto.setPath(texture.getPath());

        return dto;
    }

    public static GameObjectDTO convert(GameObject gameObject) {
        GameObjectDTO dto = new GameObjectDTO();
        dto.setId(gameObject.getId());
        dto.setName(gameObject.getName());
        dto.setActive(gameObject.isActive());

        // position
        dto.getTrans()[0] = gameObject.position.x;
        dto.getTrans()[1] = gameObject.position.y;
        dto.getTrans()[2] = gameObject.position.z;

        // rotation
        dto.getTrans()[3] = gameObject.rotation.getYaw();
        dto.getTrans()[4] = gameObject.rotation.getPitch();
        dto.getTrans()[5] = gameObject.rotation.getRoll();

        // scl
        dto.getTrans()[6] = gameObject.scale.x;
        dto.getTrans()[7] = gameObject.scale.y;
        dto.getTrans()[8] = gameObject.scale.z;

        // components
        for(Component c : gameObject.getComponents()) {
            if(c.getType() == Component.Type.MODEL) {
                dto.setModelC(convert((ModelComponent)c));
            } else if(c.getType() == Component.Type.TERRAIN) {
                dto.setTerrC(convert((TerrainComponent) c));
            }
        }


        // TODO TAGS !!!!!!!!!!!!!!!!!!!!!!!!!!!

        // recursively convert children
        if(gameObject.getChilds() != null) {
            GameObjectDTO[] childs = new GameObjectDTO[gameObject.getChilds().size];
            for(int i = 0; i < childs.length; i++) {
                childs[i] = convert(gameObject.getChilds().get(i));
            }
            dto.setChilds(childs);
        }

        return dto;
    }


}
