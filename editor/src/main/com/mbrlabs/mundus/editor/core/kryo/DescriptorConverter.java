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

package com.mbrlabs.mundus.editor.core.kryo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonWriter;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.editor.core.EditorScene;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.BaseLightDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.FogDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.GameObjectDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.ModelComponentDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.ProjectDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.ProjectRefDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.ProjectSettingsDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.RegistryDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.SceneDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.SettingsDescriptor;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.TerrainComponentDescriptor;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectSettings;
import com.mbrlabs.mundus.editor.core.registry.KeyboardLayout;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.core.registry.Settings;
import com.mbrlabs.mundus.editor.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.editor.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.editor.utils.Log;

import java.util.Locale;
import java.util.Map;

/**
 * Converts runtime formats into Kryo compatible formats for internal project
 * persistence.
 *
 * @author Marcus Brummer
 * @version 17-12-2015
 */
public class DescriptorConverter {

    private final static String TAG = DescriptorConverter.class.getSimpleName();

    private static final Vector3 tempVec = new Vector3();
    private static final Quaternion tempQuat = new Quaternion();

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Registry
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static RegistryDescriptor convert(Registry registry) {
        RegistryDescriptor descriptor = new RegistryDescriptor();

        descriptor.setLastProject(convert(registry.getLastOpenedProject()));
        for (ProjectRef projectRef : registry.getProjects()) {
            descriptor.getProjects().add(convert(projectRef));
        }
        descriptor.setSettingsDescriptor(convert(registry.getSettings()));

        return descriptor;
    }

    public static Registry convert(RegistryDescriptor descriptor) {
        Registry registry = new Registry();

        registry.setLastProject(convert(descriptor.getLastProject()));
        for (ProjectRefDescriptor projectRef : descriptor.getProjects()) {
            registry.getProjects().add(convert(projectRef));
        }
        registry.setSettings(convert(descriptor.getSettingsDescriptor()));

        return registry;
    }

    private static ProjectRef convert(ProjectRefDescriptor descriptor) {
        ProjectRef project = new ProjectRef();
        project.setName(descriptor.getName());
        project.setPath(descriptor.getPath());

        return project;
    }

    private static ProjectRefDescriptor convert(ProjectRef project) {
        ProjectRefDescriptor descriptor = new ProjectRefDescriptor();
        descriptor.setPath(project.getPath());
        descriptor.setName(project.getName());

        return descriptor;
    }

    private static Settings convert(SettingsDescriptor descriptor) {
        Settings settings = new Settings();
        settings.setFbxConvBinary(descriptor.getFbxConvBinary());
        settings.setKeyboardLayout(descriptor.getKeyboardLayout());

        if (settings.getKeyboardLayout() == null) {
            if (Locale.getDefault().equals(Locale.GERMAN) || Locale.getDefault().equals(Locale.GERMANY)) {
                settings.setKeyboardLayout(KeyboardLayout.QWERTZ);
            } else {
                settings.setKeyboardLayout(KeyboardLayout.QWERTY);
            }
        }

        return settings;
    }

    private static SettingsDescriptor convert(Settings settings) {
        SettingsDescriptor descriptor = new SettingsDescriptor();
        descriptor.setKeyboardLayout(settings.getKeyboardLayout());
        descriptor.setFbxConvBinary(settings.getFbxConvBinary());

        return descriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Game Object
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static GameObject convert(GameObjectDescriptor descriptor, SceneGraph sceneGraph,
            Map<String, Asset> assets) {
        final GameObject go = new GameObject(sceneGraph, descriptor.getName(), descriptor.getId());
        go.active = descriptor.isActive();

        // transformation
        final float[] transform = descriptor.getTransform();
        go.translate(transform[0], transform[1], transform[2]);
        go.rotate(transform[3], transform[4], transform[5], transform[6]);
        go.scale(transform[7], transform[8], transform[9]);

        // TODO TAGS !!!!!!!!!!!!!!!!!!!!!!!!!!!

        // convert components
        if (descriptor.getModelComponent() != null) {
            go.getComponents().add(convert(descriptor.getModelComponent(), go, assets));
        } else if (descriptor.getTerrainComponent() != null) {
            go.getComponents().add(convert(descriptor.getTerrainComponent(), go, assets));
        }

        // recursively convert children
        if (descriptor.getChilds() != null) {
            for (GameObjectDescriptor c : descriptor.getChilds()) {
                go.addChild(convert(c, sceneGraph, assets));
            }
        }

        return go;
    }

    public static GameObjectDescriptor convert(GameObject go) {

        GameObjectDescriptor descriptor = new GameObjectDescriptor();
        descriptor.setName(go.name);
        descriptor.setId(go.id);
        descriptor.setActive(go.active);

        // translation
        go.getLocalPosition(tempVec);
        final float[] transform = descriptor.getTransform();
        transform[0] = tempVec.x;
        transform[1] = tempVec.y;
        transform[2] = tempVec.z;

        // rotation
        go.getLocalRotation(tempQuat);
        transform[3] = tempQuat.x;
        transform[4] = tempQuat.y;
        transform[5] = tempQuat.z;
        transform[6] = tempQuat.w;

        // scaling
        go.getLocalScale(tempVec);
        transform[7] = tempVec.x;
        transform[8] = tempVec.y;
        transform[9] = tempVec.z;

        // convert components
        for (Component c : go.getComponents()) {
            if (c.getType() == Component.Type.MODEL) {
                descriptor.setModelComponent(convert((ModelComponent) c));
            } else if (c.getType() == Component.Type.TERRAIN) {
                descriptor.setTerrainComponent(convert((TerrainComponent) c));
            }
        }

        // TODO TAGS !!!!!!!!!!!!!!!!!!!!!!!!!!!

        // recursively convert children
        if (go.getChildren() != null) {
            for (GameObject c : go.getChildren()) {
                descriptor.getChilds().add(convert(c));
            }
        }

        return descriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // ModelComponent
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ModelComponent convert(ModelComponentDescriptor descriptor, GameObject go,
            Map<String, Asset> assets) {
        ModelAsset model = (ModelAsset) assets.get(descriptor.getModelID());

        if (model == null) {
            Log.fatal(TAG, "MModel for MModelInstance not found: {}", descriptor.getModelID());
            return null;
        }

        ModelComponent component = new ModelComponent(go);
        component.setModel(model, false);

        for (String g3dbMatID : descriptor.getMaterials().keySet()) {
            String uuid = descriptor.getMaterials().get(g3dbMatID);
            MaterialAsset matAsset = (MaterialAsset) assets.get(uuid);
            component.getMaterials().put(g3dbMatID, matAsset);
        }

        return component;
    }

    public static ModelComponentDescriptor convert(ModelComponent modelComponent) {
        ModelComponentDescriptor descriptor = new ModelComponentDescriptor();
        descriptor.setModelID(modelComponent.getModelAsset().getID());

        // materials
        for (String g3dbMatID : modelComponent.getMaterials().keySet()) {
            descriptor.getMaterials().put(g3dbMatID, modelComponent.getMaterials().get(g3dbMatID).getID());
        }

        return descriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // TerrainComponent
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static TerrainComponent convert(TerrainComponentDescriptor descriptor, GameObject go,
            Map<String, Asset> assets) {
        // find terrainAsset
        TerrainAsset terrain = (TerrainAsset) assets.get(descriptor.getTerrainID());

        if (terrain == null) {
            Log.fatal(TAG, "Terrain for TerrainInstance not found");
            return null;
        }

        terrain.getTerrain().transform = go.getTransform();
        TerrainComponent terrainComponent = new TerrainComponent(go);
        terrainComponent.setTerrain(terrain);

        return terrainComponent;
    }

    public static TerrainComponentDescriptor convert(TerrainComponent terrainComponent) {
        TerrainComponentDescriptor descriptor = new TerrainComponentDescriptor();
        descriptor.setTerrainID(terrainComponent.getTerrain().getID());

        return descriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fog
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Fog convert(FogDescriptor fogDescriptor) {
        if (fogDescriptor == null) return null;
        Fog fog = new Fog();
        fog.density = fogDescriptor.getDensity();
        fog.gradient = fogDescriptor.getGradient();
        fog.color = new Color(fogDescriptor.getColor());

        return fog;
    }

    public static FogDescriptor convert(Fog fog) {
        if (fog == null) return null;
        FogDescriptor fogDescriptor = new FogDescriptor();
        fogDescriptor.setDensity(fog.density);
        fogDescriptor.setGradient(fog.gradient);
        fogDescriptor.setColor(Color.rgba8888(fog.color));

        return fogDescriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Base light
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static BaseLight convert(BaseLightDescriptor lightDescriptor) {
        if (lightDescriptor == null) return null;
        BaseLight light = new BaseLight();
        light.intensity = lightDescriptor.getIntensity();
        light.color.set(lightDescriptor.getColor());

        return light;
    }

    public static BaseLightDescriptor convert(BaseLight light) {
        if (light == null) return null;
        BaseLightDescriptor lightDescriptor = new BaseLightDescriptor();
        lightDescriptor.setIntensity(light.intensity);
        lightDescriptor.setColor(Color.rgba8888(light.color));

        return lightDescriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Scene
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static SceneDescriptor convert(Scene scene) {
        SceneDescriptor descriptor = new SceneDescriptor();

        // meta
        descriptor.setName(scene.getName());
        descriptor.setId(scene.getId());

        // scene graph
        for (GameObject go : scene.sceneGraph.getGameObjects()) {
            descriptor.getGameObjects().add(convert(go));
        }

        // environment stuff
        descriptor.setFog(convert(scene.environment.getFog()));
        descriptor.setAmbientLight(convert(scene.environment.getAmbientLight()));

        // camera
        descriptor.setCamPosX(scene.cam.position.x);
        descriptor.setCamPosY(scene.cam.position.y);
        descriptor.setCamPosZ(scene.cam.position.z);
        descriptor.setCamDirX(scene.cam.direction.x);
        descriptor.setCamDirY(scene.cam.direction.y);
        descriptor.setCamDirZ(scene.cam.direction.z);
        return descriptor;
    }

    public static EditorScene convert(SceneDescriptor sceneDescriptor, Map<String, Asset> assets) {
        EditorScene scene = new EditorScene();

        // meta
        scene.setId(sceneDescriptor.getId());
        scene.setName(sceneDescriptor.getName());

        // environment stuff
        scene.environment.setFog(convert(sceneDescriptor.getFog()));
        BaseLight ambientLight = convert(sceneDescriptor.getAmbientLight());
        if (ambientLight != null) {
            scene.environment.setAmbientLight(ambientLight);
        }

        // scene graph
        scene.sceneGraph = new SceneGraph(scene);
        for (GameObjectDescriptor descriptor : sceneDescriptor.getGameObjects()) {
            scene.sceneGraph.addGameObject(convert(descriptor, scene.sceneGraph, assets));
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Project
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ProjectDescriptor convert(ProjectContext project) {
        ProjectDescriptor descriptor = new ProjectDescriptor();
        descriptor.setName(project.name);
        descriptor.setCurrentSceneName(project.currScene.getName());
        descriptor.setNextAvailableID(project.inspectCurrentID());

        // scenes
        for (String sceneName : project.scenes) {
            descriptor.getSceneNames().add(sceneName);
        }

        return descriptor;
    }

    public static ProjectContext convert(ProjectDescriptor projectDescriptor) {
        ProjectContext context = new ProjectContext(projectDescriptor.getNextAvailableID());
        context.name = projectDescriptor.getName();

        // scenes
        for (String sceneName : projectDescriptor.getSceneNames()) {
            context.scenes.add(sceneName);
        }

        return context;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Project Settings
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ProjectSettingsDescriptor convert(ProjectSettings settings) {
        ProjectSettingsDescriptor descriptor = new ProjectSettingsDescriptor();

        // export settings
        descriptor.setExportAllAssets(settings.getExport().allAssets);
        descriptor.setExportCompressScenes(settings.getExport().compressScenes);
        descriptor.setExportOutputFolder(settings.getExport().outputFolder);
        descriptor.setJsonType(settings.getExport().jsonType.toString());

        return descriptor;
    }

    public static ProjectSettings convert(ProjectSettingsDescriptor descriptor) {
        ProjectSettings settings = new ProjectSettings();

        // export settings
        settings.getExport().allAssets = descriptor.isExportAllAssets();
        settings.getExport().compressScenes = descriptor.isExportCompressScenes();
        settings.getExport().outputFolder = descriptor.getExportOutputFolder();
        settings.getExport().jsonType = JsonWriter.OutputType.valueOf(descriptor.getJsonType());

        return settings;
    }

}
