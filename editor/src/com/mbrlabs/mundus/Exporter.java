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

package com.mbrlabs.mundus;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.exporter.dto.*;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.model.MModelInstance;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.terrain.TerrainInstance;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class Exporter {

    public Exporter() {

    }

    public static ModelDTO convert(MModel model) {
        ModelDTO dto = new ModelDTO();
        dto.setName(model.name);
        dto.setId(model.id);
        return dto;
    }

    public static ModelInstanceDTO convert(MModelInstance modelInstance) {
        Vector3 vec3 = new Vector3();
        Quaternion quat = new Quaternion();

        ModelInstanceDTO dto = new ModelInstanceDTO();
        dto.setModelID(modelInstance.getModelId());

        // translation
        modelInstance.modelInstance.transform.getTranslation(vec3);
        dto.getPosition()[0] = vec3.x;
        dto.getPosition()[1] = vec3.y;
        dto.getPosition()[2] = vec3.z;
        // rotation
        modelInstance.modelInstance.transform.getRotation(quat);
        dto.getRotation()[0] = quat.x;
        dto.getRotation()[1] = quat.y;
        dto.getRotation()[2] = quat.z;
        // scaling
        modelInstance.modelInstance.transform.getScale(vec3);
        dto.getScale()[0] = vec3.x;
        dto.getScale()[1] = vec3.y;
        dto.getScale()[2] = vec3.z;

        return dto;
    }

    public static TerrainDTO convert(Terrain terrain) {
        TerrainDTO dto = new TerrainDTO();
        dto.setId(terrain.id);
        dto.setName(terrain.name);
        dto.setWidth(terrain.terrainWidth);
        dto.setDepth(terrain.terrainDepth);
        dto.setVertexResolution(terrain.vertexResolution);
        return dto;
    }

    public static TerrainInstanceDTO convert(TerrainInstance terrain) {
        TerrainInstanceDTO dto = new TerrainInstanceDTO();
        dto.setId(terrain.id);
        dto.setTerrainID(terrain.terrain.id);
        dto.getPosition()[0] = terrain.getPosition().x;
        dto.getPosition()[1] = terrain.getPosition().y;
        dto.getPosition()[2] = terrain.getPosition().z;

        return dto;
    }

    public static SceneDTO convert(Scene scene) {
        // TODO enviroenment
        SceneDTO dto = new SceneDTO();
        dto.setName(scene.getName());
        dto.setId(scene.getId());

        // entities
        for(MModelInstance entity : scene.entities) {
            dto.getEntities().add(convert(entity));
        }

        // terrains
        for(TerrainInstance terrain : scene.terrainGroup.getTerrains()) {
            dto.getTerrains().add(convert(terrain));
        }


        return dto;
    }

    public static ProjectDTO convert(ProjectContext project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setName(project.name);
        // terrains
        for(Terrain terrain : project.terrains) {
            dto.getTerrains().add(convert(terrain));
        }
        // models
        for(MModel model : project.models) {
            dto.getModels().add(convert(model));
        }
        // scenes
        for(Scene scene : project.scenes) {
            dto.getScenes().add(convert(scene));
        }

        return dto;
    }


}
