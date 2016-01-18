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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.core.kryo.descriptors.*;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.model.MModelInstance;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainInstance;
import com.mbrlabs.mundus.model.MTexture;
import com.mbrlabs.mundus.scene3d.Component;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.scene3d.ModelComponent;
import com.mbrlabs.mundus.scene3d.SceneGraph;
import com.mbrlabs.mundus.utils.Log;

/**
 * Converts runtime formats into Kryo compatible formats for internal
 * project persistence.
 *
 * @author Marcus Brummer
 * @version 17-12-2015
 */
public class DescriptorConverter {

    private static final Vector3 tempV3 = new Vector3();
    private static final Quaternion tempQuat = new Quaternion();

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     Model
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ModelDescriptor convert(MModel model) {
        ModelDescriptor descriptor = new ModelDescriptor();
        descriptor.setName(model.name);
        descriptor.setId(model.id);
        descriptor.setG3dbPath(model.g3dbPath);
        descriptor.setTexturePath(model.texturePath);
        return descriptor;
    }

    public static MModel convert(ModelDescriptor modelDescriptor) {
        MModel model = new MModel();
        model.id = modelDescriptor.getId();
        model.name = modelDescriptor.getName();
        model.g3dbPath = modelDescriptor.getG3dbPath();
        model.texturePath = modelDescriptor.getTexturePath();
        return model;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     Game Object
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static GameObject convert(GameObjectDescriptor descriptor, SceneGraph sceneGraph, Array<MModel> models) {
        final GameObject go = new GameObject(sceneGraph, descriptor.getName(), descriptor.getId());

        final float[] pos = descriptor.getPosition();
        final float[] rot = descriptor.getRotation();
        final float[] scl = descriptor.getScale();

        go.transform.translate(pos[0], pos[1], pos[2]);
        go.transform.rotate(rot[0], rot[1], rot[2], 0);
        go.transform.scl(scl[0], scl[0], scl[0]);

        // TODO TAGS !!!!!!!!!!!!!!!!!!!!!!!!!!!

        // convert components
        if(descriptor.getModelComponent() != null) {
            go.addComponent(convert(descriptor.getModelComponent(), go, models));
        }

        // recursively convert children
        if(descriptor.getChilds() != null) {
            for (GameObjectDescriptor c : descriptor.getChilds()) {
                go.addChild(convert(c, sceneGraph, models));
            }
        }

        return go;
    }

    public static GameObjectDescriptor convert(GameObject go) {
        GameObjectDescriptor descriptor = new GameObjectDescriptor();
        descriptor.setName(go.getName());
        descriptor.setId(go.getId());

        // translation
        go.transform.getTranslation(tempV3);
        descriptor.getPosition()[0] = tempV3.x;
        descriptor.getPosition()[1] = tempV3.y;
        descriptor.getPosition()[2] = tempV3.z;

        // rotation
        go.transform.getRotation(tempQuat);
        descriptor.getRotation()[0] = tempQuat.x;
        descriptor.getRotation()[1] = tempQuat.y;
        descriptor.getRotation()[2] = tempQuat.z;

        // scaling
        go.transform.getScale(tempV3);
        descriptor.getScale()[0] = tempV3.x;
        descriptor.getScale()[1] = tempV3.y;
        descriptor.getScale()[2] = tempV3.z;

        // convert components
        for(Component c : go.getComponents()) {
            if(c.getType() == Component.Type.MODEL) {
                descriptor.setModelComponent(convert((ModelComponent) c));
            }
        }

        // TODO TAGS !!!!!!!!!!!!!!!!!!!!!!!!!!!

        // recursively convert children
        if(go.getChilds() != null) {
            for(GameObject c : go.getChilds()) {
                descriptor.getChilds().add(convert(c));
            }
        }

        return descriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     ModelComponent
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ModelComponent convert(ModelComponentDescriptor descriptor, GameObject go, Array<MModel> models) {
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

        MModelInstance modelInstance = new MModelInstance(model);

        ModelComponent component = new ModelComponent(go);
        component.setModel(modelInstance);

        return component;
    }

    public static ModelComponentDescriptor convert(ModelComponent modelComponent) {
        ModelComponentDescriptor descriptor = new ModelComponentDescriptor();
        descriptor.setModelID(modelComponent.getModel().getModelId());

        return descriptor;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //                               Terrain & TerrainInstance
    /////////////////////////////////////////////////////////////////////////////////////////////////////

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

    public static TerrainInstance convert(TerrainInstanceDescriptor terrainDescriptor, Array<Terrain> terrains) {
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                       Texture
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static MTexture convert(TextureDescriptor textureDescriptor) {
        MTexture tex = new MTexture();
        tex.path = textureDescriptor.getPath();
        tex.setId(textureDescriptor.getId());

        return tex;
    }

    public static TextureDescriptor convert(MTexture tex) {
        TextureDescriptor textureDescriptor = new TextureDescriptor();
        textureDescriptor.setId(tex.getId());
        textureDescriptor.setName(tex.getName());
        textureDescriptor.setPath(tex.path);

        return textureDescriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                              Fog
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Fog convert(FogDescriptor fogDescriptor) {
        if(fogDescriptor == null) return null;
        Fog fog = new Fog();
        fog.density = fogDescriptor.getDensity();
        fog.gradient = fogDescriptor.getGradient();
        fog.color = new Color(fogDescriptor.getColor());

        return fog;
    }

    public static FogDescriptor convert(Fog fog) {
        if(fog == null) return null;
        FogDescriptor fogDescriptor = new FogDescriptor();
        fogDescriptor.setDensity(fog.density);
        fogDescriptor.setGradient(fog.gradient);
        fogDescriptor.setColor(Color.rgba8888(fog.color));

        return fogDescriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          Scene
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static SceneDescriptor convert(Scene scene) {
        // TODO enviroenment
        SceneDescriptor descriptor = new SceneDescriptor();

        // meta
        descriptor.setName(scene.getName());
        descriptor.setId(scene.getId());

        // scene graph
        descriptor.setSceneGraphRoot(convert(scene.sceneGraph.getRoot()));

        // fog
        descriptor.setFog(convert(scene.environment.getFog()));


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

    public static Scene convert(SceneDescriptor sceneDescriptor, Array<Terrain> terrains, Array<MModel> models) {
        // TODO enviroenment
        Scene scene = new Scene();

        // meta
        scene.setId(sceneDescriptor.getId());
        scene.setName(sceneDescriptor.getName());

        // fog
        scene.environment.setFog(convert(sceneDescriptor.getFog()));

        // terrains
        for(TerrainInstanceDescriptor terrainDescriptor : sceneDescriptor.getTerrains()) {
            scene.terrainGroup.add(convert(terrainDescriptor, terrains));
        }

        // scene graph
        scene.sceneGraph = new SceneGraph(scene);
        scene.sceneGraph.setRoot(convert(sceneDescriptor.getSceneGraphRoot(), scene.sceneGraph, models));

        // camera
        scene.cam.position.x = sceneDescriptor.getCamPosX();
        scene.cam.position.y = sceneDescriptor.getCamPosY();
        scene.cam.position.z = sceneDescriptor.getCamPosZ();
        scene.cam.direction.set(sceneDescriptor.getCamDirX(), sceneDescriptor.getCamDirY(),
                sceneDescriptor.getCamDirZ());
        scene.cam.update();

        return scene;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          Project
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ProjectDescriptor convert(ProjectContext project) {
        ProjectDescriptor descriptor = new ProjectDescriptor();
        descriptor.setName(project.name);
        descriptor.setId(project.id);
        descriptor.setCurrentSceneID(project.currScene.getId());
        descriptor.setNextAvailableID(project.getCurrentUUID());

        // textures
        for(MTexture texture : project.textures) {
            descriptor.getTextures().add(convert(texture));
        }
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

        // textures
        for(TextureDescriptor texture : projectDescriptor.getTextures()) {
            context.textures.add(convert(texture));
        }
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
