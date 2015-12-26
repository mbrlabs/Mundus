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

package com.mbrlabs.mundus.core.kryo;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.core.kryo.descriptors.*;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.model.MModelInstance;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.terrain.TerrainInstance;
import com.mbrlabs.mundus.utils.Log;

import java.util.List;

/**
 * @author Marcus Brummer
 * @version 17-12-2015
 */
public class DescriptorConverter {

    public static ModelDescriptor convert(MModel model) {
        ModelDescriptor descriptor = new ModelDescriptor();
        descriptor.setName(model.name);
        descriptor.setId(model.id);
        descriptor.setG3dbPath(model.g3dbPath);
        return descriptor;
    }

    public static MModel convert(ModelDescriptor modelDescriptor) {
        MModel model = new MModel();
        model.id = modelDescriptor.getId();
        model.name = modelDescriptor.getName();
        model.g3dbPath = modelDescriptor.getG3dbPath();

        return model;
    }

    public static TerrainDescriptor convert(Terrain terrain) {
        TerrainDescriptor descriptor = new TerrainDescriptor();
        descriptor.setId(terrain.id);
        descriptor.setName(terrain.name);
        descriptor.setPath(terrain.terraPath);
        descriptor.setWidth(terrain.terrainWidth);
        descriptor.setDepth(terrain.terrainDepth);
        descriptor.setVertexResolution(terrain.vertexResolution);
        return descriptor;
    }

    public static Terrain convert(TerrainDescriptor terrainDescriptor) {
        Terrain terrain = new Terrain(terrainDescriptor.getVertexResolution());
        terrain.terrainWidth = terrainDescriptor.getWidth();
        terrain.terrainDepth = terrainDescriptor.getDepth();
        terrain.terraPath = terrainDescriptor.getPath();
        terrain.id = terrainDescriptor.getId();
        terrain.name = terrainDescriptor.getName();

        return terrain;
    }

    public static TerrainInstanceDescriptor convert(TerrainInstance terrain) {
        TerrainInstanceDescriptor descriptor = new TerrainInstanceDescriptor();
        descriptor.setId(terrain.id);
        descriptor.setName(terrain.name);
        descriptor.setTerrainID(terrain.terrain.id);
        Vector3 pos = terrain.getPosition();
        descriptor.setPosX(pos.x);
        descriptor.setPosZ(pos.z);

        return descriptor;
    }

    public static TerrainInstance convert(TerrainInstanceDescriptor terrainDescriptor, List<Terrain> terrains) {
        // find terrain
        Terrain terrain = null;
        for(Terrain t : terrains) {
            if(terrainDescriptor.getTerrainID() == t.id) {
                terrain = t;
                break;
            }
        }

        if(terrain == null) {
            Log.fatal("Terrain for TerrainInstance not found");
            return null;
        }

        final TerrainInstance terrainInstance = new TerrainInstance(terrain);
        terrainInstance.transform.setTranslation(terrainDescriptor.getPosX(), 0, terrainDescriptor.getPosZ());
        terrainInstance.name = terrainDescriptor.getName();
        terrainInstance.id = terrainDescriptor.getId();

        return terrainInstance;
    }

    public static MModelInstance convert(ModelInstanceDescriptor descriptor, List<MModel> models) {
        // find model
        MModel model = null;
        for(MModel m : models) {
            if(descriptor.getModelID() == m.id) {
                model = m;
                break;
            }
        }

        if(model == null) {
            Log.fatal("MModel for MModelInstance not found: " + descriptor.getModelID());
            return null;
        }

        MModelInstance mModelInstance = new MModelInstance(model);
        mModelInstance.kryoTransform.translate(descriptor.getPosX(), descriptor.getPosY(), descriptor.getPosZ());
        mModelInstance.kryoTransform.rotate(descriptor.getRotX(), descriptor.getRotY(), descriptor.getRotZ(), 0);
        mModelInstance.kryoTransform.scl(descriptor.getScaleX(), descriptor.getScaleY(), descriptor.getScaleZ());
        return mModelInstance;
    }

    public static ModelInstanceDescriptor convert(MModelInstance modelInstance) {
        Vector3 vec3 = new Vector3();
        Quaternion quat = new Quaternion();

        ModelInstanceDescriptor descriptor = new ModelInstanceDescriptor();
        descriptor.setModelID(modelInstance.getModelId());

        // translation
        modelInstance.modelInstance.transform.getTranslation(vec3);
        descriptor.setPosX(vec3.x);
        descriptor.setPosY(vec3.y);
        descriptor.setPosZ(vec3.z);
        // rotation
        modelInstance.modelInstance.transform.getRotation(quat);
        descriptor.setRotX(quat.x);
        descriptor.setRotY(quat.y);
        descriptor.setRotZ(quat.z);
        // scaling
        modelInstance.modelInstance.transform.getScale(vec3);
        descriptor.setScaleX(vec3.x);
        descriptor.setScaleY(vec3.y);
        descriptor.setScaleZ(vec3.z);

        return descriptor;
    }

    public static SceneDescriptor convert(Scene scene) {
        // TODO enviroenment
        SceneDescriptor descriptor = new SceneDescriptor();
        descriptor.setName(scene.getName());
        descriptor.setId(scene.getId());

        // entities
        for(MModelInstance entity : scene.entities) {
            descriptor.getEntities().add(convert(entity));
        }

        // terrains
        for(TerrainInstance terrain : scene.terrainGroup.getTerrains()) {
            descriptor.getTerrains().add(convert(terrain));
        }

        // camera
        descriptor.setCamPosX(scene.cam.position.x);
        descriptor.setCamPosY(scene.cam.position.y);
        descriptor.setCamPosZ(scene.cam.position.z);
        descriptor.setCamDirX(scene.cam.direction.x);
        descriptor.setCamDirY(scene.cam.direction.y);
        descriptor.setCamDirZ(scene.cam.direction.z);
        return descriptor;
    }

    public static Scene convert(SceneDescriptor sceneDescriptor, List<Terrain> terrains, List<MModel> models) {
        // TODO enviroenment
        Scene scene = new Scene();
        scene.setId(sceneDescriptor.getId());
        scene.setName(sceneDescriptor.getName());

        // terrains
        for(TerrainInstanceDescriptor terrainDescriptor : sceneDescriptor.getTerrains()) {
            scene.terrainGroup.add(convert(terrainDescriptor, terrains));
        }

        // entities
        for(ModelInstanceDescriptor descriptor : sceneDescriptor.getEntities()) {
            scene.entities.add(convert(descriptor, models));
        }

        // camera
        scene.cam.position.x = sceneDescriptor.getCamPosX();
        scene.cam.position.y = sceneDescriptor.getCamPosY();
        scene.cam.position.z = sceneDescriptor.getCamPosZ();
        scene.cam.direction.set(sceneDescriptor.getCamDirX(), sceneDescriptor.getCamDirY(),
                sceneDescriptor.getCamDirZ());
        scene.cam.update();


        return scene;
    }


    public static ProjectDescriptor convert(ProjectContext project) {
        ProjectDescriptor descriptor = new ProjectDescriptor();
        descriptor.setName(project.name);
        descriptor.setId(project.id);
        descriptor.setCurrentSceneID(project.currScene.getId());
        descriptor.setNextAvailableID(project.getCurrentUUID());
        // terrains
        for(Terrain terrain : project.terrains) {
            descriptor.getTerrains().add(convert(terrain));
        }
        // models
        for(MModel model : project.models) {
            descriptor.getModels().add(convert(model));
        }
        // scenes
        for(Scene scene : project.scenes) {
            descriptor.getScenes().add(convert(scene));
        }

        return descriptor;
    }

    public static ProjectContext convert(ProjectDescriptor projectDescriptor) {
        ProjectContext context = new ProjectContext(projectDescriptor.getNextAvailableID());
        context.name = projectDescriptor.getName();
        // models
        for(ModelDescriptor model : projectDescriptor.getModels()) {
            context.models.add(convert(model));
        }
        // terrains
        for(TerrainDescriptor terrain : projectDescriptor.getTerrains()) {
            context.terrains.add(convert(terrain));
        }
        // scenes
        for(SceneDescriptor scene : projectDescriptor.getScenes()) {
            Scene s = convert(scene, context.terrains, context.models);
            context.scenes.add(s);

            // set current scene
            if(scene.getId() == projectDescriptor.getCurrentSceneID()) {
                context.currScene = s;
            }
        }

        context.loaded = false;
        return context;
    }




}
