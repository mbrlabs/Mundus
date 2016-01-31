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

package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.utils.TextureUtils;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.core.kryo.DescriptorConverter;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.core.kryo.descriptors.HomeDescriptor;
import com.mbrlabs.mundus.core.kryo.descriptors.SceneDescriptor;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.SceneChangedEvent;
import com.mbrlabs.mundus.exceptions.ProjectAlreadyImportedException;
import com.mbrlabs.mundus.exceptions.ProjectOpenException;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.scene3d.*;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.terrain.TerrainIO;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.utils.SkyboxBuilder;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager {

    public static final String PROJECT_ASSETS_DIR           =    "assets/";
    public static final String PROJECT_MODEL_DIR            =    PROJECT_ASSETS_DIR + "/models/";
    public static final String PROJECT_TERRAIN_DIR          =    PROJECT_ASSETS_DIR + "/terrains/";
    public static final String PROJECT_TEXTURE_DIR          =    PROJECT_ASSETS_DIR + "/textures/";

    public static final String PROJECT_SCENES_DIR           =    "scenes/";
    public static final String PROJECT_SCENE_EXTENSION      =    ".mundus";


    private static final String DEFAULT_SCENE_NAME = "Main Scene";

    private ProjectContext projectContext;
    private HomeManager homeManager;
    private KryoManager kryoManager;

    private ToolManager toolManager;
    private ModelBatch modelBatch;
    private Shaders shaders;

    public ProjectManager(ProjectContext projectContext, KryoManager kryoManager,
                          HomeManager homeManager, ToolManager toolManager, ModelBatch batch, Shaders shaders) {
        this.projectContext = projectContext;
        this.homeManager = homeManager;
        this.kryoManager = kryoManager;
        this.toolManager = toolManager;
        this.modelBatch = batch;
        this.shaders = shaders;
    }

    public ProjectContext createProject(String name, String folder) {
        HomeDescriptor.ProjectRef ref = homeManager.createProjectRef(name, folder);
        String path = ref.getAbsolutePath();
        new File(path).mkdirs();
        new File(path, PROJECT_MODEL_DIR).mkdirs();
        new File(path, PROJECT_TERRAIN_DIR).mkdirs();
        new File(path, PROJECT_TEXTURE_DIR).mkdirs();
        new File(path, PROJECT_SCENES_DIR).mkdirs();

        // create project context
        ProjectContext newProjectContext = new ProjectContext(-1);
        newProjectContext.absolutePath = path;
        newProjectContext.name = ref.getName();

        // create default scene & save .mundus
        Scene scene = new Scene();
        scene.setName(DEFAULT_SCENE_NAME);
        scene.skybox = SkyboxBuilder.createDefaultSkybox();
        scene.environment.setFog(new Fog());
        scene.setId(newProjectContext.obtainUUID());
        kryoManager.saveScene(newProjectContext, scene);
        scene.sceneGraph.batch = Mundus.modelBatch;

        // save .pro file
        newProjectContext.scenes.add(scene.getName());
        newProjectContext.currScene = scene;
        saveProject(newProjectContext);

        return newProjectContext;
    }

    public ProjectContext importProject(String absolutePath) throws ProjectAlreadyImportedException, ProjectOpenException {
        // check if already imported
        for (HomeDescriptor.ProjectRef ref : homeManager.homeDescriptor.projects) {
            if (ref.getAbsolutePath().equals(absolutePath)) {
                throw new ProjectAlreadyImportedException("Project " + absolutePath + " is already imported");
            }
        }

        HomeDescriptor.ProjectRef ref = new HomeDescriptor.ProjectRef();
        ref.setAbsolutePath(absolutePath);

        try {
            ProjectContext context = loadProject(ref);
            ref.setName(context.name);
            homeManager.homeDescriptor.projects.add(ref);
            homeManager.save();
            return context;
        } catch (Exception e) {
            throw new ProjectOpenException(e.getMessage());
        }
    }

    public ProjectContext loadProject(HomeDescriptor.ProjectRef ref) throws FileNotFoundException {
        ProjectContext context = kryoManager.loadProjectContext(ref);
        context.absolutePath = ref.getAbsolutePath();

        // load textures
        for(MTexture tex : context.textures) {
            tex.texture = TextureUtils.loadMipmapTexture(
                    Gdx.files.absolute(FilenameUtils.concat(context.absolutePath, tex.getPath())));
            Log.debug("Loaded texture: " + tex.getPath());
        }

        // load g3db models
        G3dModelLoader loader = new G3dModelLoader(new UBJsonReader());
        for(MModel model : context.models) {
            model.setModel(loader.loadModel(Gdx.files.absolute(
                    FilenameUtils.concat(context.absolutePath, model.g3dbPath))));
        }

        // load terrain .terra files
        for(Terrain terrain : context.terrains) {
            TerrainIO.importTerrain(terrain, terrain.terraPath);
        }

        context.currScene = loadScene(context, context.kryoActiveScene);

        return context;
    }

    public void saveProject(ProjectContext projectContext) {
        // save .terra files & the splat map
        for(Terrain terrain : projectContext.terrains) {
            String path = FilenameUtils.concat(projectContext.absolutePath, ProjectManager.PROJECT_TERRAIN_DIR);
            path += terrain.id + "." + TerrainIO.FILE_EXTENSION;
            terrain.terraPath = path;
            TerrainIO.exportTerrain(terrain, path);
        }

        // save context in .pro file
        kryoManager.saveProjectContext(projectContext);
        // save scene in .mundus file
        kryoManager.saveScene(projectContext, projectContext.currScene);

        Log.debug("Saving project " + projectContext.name+ " [" + projectContext.absolutePath + "]");
    }

    public boolean openLastOpenedProject() {
        HomeDescriptor.ProjectRef lastOpenedProject = homeManager.getLastOpenedProject();
        if (lastOpenedProject != null) {
            try {
                ProjectContext context = loadProject(lastOpenedProject);
                if (new File(context.absolutePath).exists()) {
                    changeProject(context);
                    return true;
                } else {
                    Log.error("Failed to load last opened project");
                }
            } catch (FileNotFoundException fnf) {
                fnf.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public void changeProject(ProjectContext context) {
        toolManager.deactivateTool();
        homeManager.homeDescriptor.lastProject = new HomeDescriptor.ProjectRef();
        homeManager.homeDescriptor.lastProject.setName(context.name);
        homeManager.homeDescriptor.lastProject.setAbsolutePath(context.absolutePath);
        homeManager.save();
        projectContext.dispose();
        projectContext.copyFrom(context);
        Gdx.graphics.setTitle(constructWindowTitle());
        Mundus.postEvent(new ProjectChangedEvent());
        toolManager.setDefaultTool();
    }

    public Scene createScene(ProjectContext projectContext, String name) {
        Scene scene = new Scene();
        long id = projectContext.obtainUUID();
        scene.setId(id);
        scene.setName(name);
        scene.skybox = SkyboxBuilder.createDefaultSkybox();
        projectContext.scenes.add(scene.getName());
        kryoManager.saveScene(projectContext, scene);

        return scene;
    }

    public Scene loadScene(ProjectContext context, String sceneName) throws FileNotFoundException {
        SceneDescriptor descriptor = kryoManager.loadScene(context, sceneName);
        Scene scene = DescriptorConverter.convert(descriptor, context.terrains, context.models);
        scene.skybox = SkyboxBuilder.createDefaultSkybox();

        SceneGraph sceneGraph = scene.sceneGraph;
        sceneGraph.batch = Mundus.modelBatch;
        initSceneGraph(context, sceneGraph.getRoot());

        // create TerrainGroup for active scene
        Array<GameObject> gos = new Array<>();
        sceneGraph.getTerrainGOs(gos);
        for(GameObject go : gos) {
            Component terrainComp = go.findComponentByType(Component.Type.TERRAIN);
            if(terrainComp != null) {
                scene.terrainGroup.add(((TerrainComponent)terrainComp).getTerrain());
            }
        }
        return scene;
    }

    public void changeScene(ProjectContext projectContext, String scenename) {
        toolManager.deactivateTool();

        try {
            Scene newScene = loadScene(projectContext, scenename);
            projectContext.currScene.dispose();
            projectContext.currScene = newScene;

            Gdx.graphics.setTitle(constructWindowTitle());
            Mundus.postEvent(new SceneChangedEvent());
            toolManager.setDefaultTool();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initSceneGraph(ProjectContext context, GameObject root) {
        initComponents(context, root);
        if(root.getChilds() != null) {
            for(GameObject c : root.getChilds()) {
                initSceneGraph(context, c);
            }
        }
    }

    private void initComponents(ProjectContext context, GameObject go) {
        for(Component c : go.getComponents()) {
            // Model component
            if(c.getType() == Component.Type.MODEL) {
                ModelComponent modelComponent = (ModelComponent) c;
                MModel model = findModelById(context.models, modelComponent.getModelInstance().getModel().id);
                if(model != null) {
                    modelComponent.getModelInstance().modelInstance = new ModelInstance(model.getModel());
                    modelComponent.getModelInstance().modelInstance.transform = go.transform;
                    modelComponent.getModelInstance().calculateBounds();
                    modelComponent.setShader(shaders.entityShader);
                } else {
                    Log.fatal("model for modelInstance not found: " + modelComponent.getModelInstance().getModel().id);
                }
            } else if(c.getType() == Component.Type.TERRAIN) {
                ((TerrainComponent)c).setShader(shaders.terrainShader);
            }
        }
    }

    private MModel findModelById(Array<MModel> models, long id) {
        for(MModel m : models) {
            if(m.id == id) {
                return m;
            }
        }

        return null;
    }

    public String constructWindowTitle() {
        return projectContext.name + " - " + projectContext.currScene.getName() +
                " [" + projectContext.absolutePath +"]" + " - " + Main.TITLE;
    }

}
