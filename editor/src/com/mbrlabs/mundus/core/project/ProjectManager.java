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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.commons.terrain.TerrainInstance;
import com.mbrlabs.mundus.core.ImportManager;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.model.MModelInstance;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.model.MTexture;
import com.mbrlabs.mundus.scene3d.*;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.terrain.TerrainIO;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.utils.Log;
import org.apache.commons.io.FilenameUtils;
import sun.security.krb5.SCDynamicStoreConfig;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager {

    public static final String PROJECT_MODEL_DIR = "models/";
    public static final String PROJECT_TERRAIN_DIR = "terrains/";
    public static final String PROJECT_TEXTURE_DIR = "textures/";

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
        ProjectRef ref = homeManager.createProjectRef(name, folder);
        String path = ref.getPath();
        new File(path).mkdirs();
        new File(path, PROJECT_MODEL_DIR).mkdirs();
        new File(path, PROJECT_TERRAIN_DIR).mkdirs();
        new File(path, PROJECT_TEXTURE_DIR).mkdirs();

        // create project context
        ProjectContext newProjectContext = new ProjectContext(-1);
        newProjectContext.path = ref.getPath();
        newProjectContext.name = ref.getName();

        // create default scene
        Scene scene = new Scene();
        scene.setName(DEFAULT_SCENE_NAME);
        scene.setId(newProjectContext.obtainUUID());

        newProjectContext.scenes.add(scene);
        newProjectContext.currScene = scene;

        // save .pro file
        saveProject(newProjectContext);


        return newProjectContext;
    }

    public ProjectContext loadProject(ProjectRef ref) {
        ProjectContext context = kryoManager.loadProjectContext(ref);
        context.path = ref.getPath();
        context.name = ref.getName();

        // load textures
        for(MTexture tex : context.textures) {
            tex.texture = new Texture(Gdx.files.absolute(FilenameUtils.concat(context.path, tex.path)));
            Log.debug("Loaded texture: " + tex.path);
        }

        // load g3db models
        G3dModelLoader loader = new G3dModelLoader(new UBJsonReader());
        for(MModel model : context.models) {
            String g3dbPath = model.g3dbPath;
            model.setModel(loader.loadModel(Gdx.files.absolute(g3dbPath)));
        }

        // load scene graph for every scene
        for(Scene scene : context.scenes) {
            scene.sceneGraph.batch = modelBatch;
            initSceneGraph(context, scene.sceneGraph.getRoot());
        }

        // load terrain .terra files
        for(Terrain terrain : context.terrains) {
            TerrainIO.importTerrain(terrain, terrain.terraPath);
        }

        // create TerrainGroup for each scene
        Array<GameObject> gos = new Array<>();
        for(Scene scene : context.scenes) {
            SceneGraph sceneGraph = scene.sceneGraph;
            gos = sceneGraph.getTerrainGOs(gos);
            for(GameObject go : gos) {
                Component terrainComp = go.getComponentByType(Component.Type.TERRAIN);
                if(terrainComp != null) {
                    scene.terrainGroup.add(((TerrainComponent)terrainComp).getTerrainInstance());
                }
            }
        }

        return context;
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
                MModel model = findModelById(context.models, modelComponent.getModel().getModelId());
                if(model != null) {
                    modelComponent.getModel().modelInstance = new ModelInstance(model.getModel());
                    modelComponent.getModel().modelInstance.transform = go.transform;
                    modelComponent.getModel().calculateBounds();
                    modelComponent.setShader(shaders.entityShader);
                } else {
                    Log.fatal("model for modelInstance not found: " + modelComponent.getModel().getModelId());
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
                " [" + projectContext.path +"]" + " - " + Main.TITLE;
    }

    public void changeProject(ProjectContext context) {
        toolManager.deactivateTool();
        homeManager.homeDescriptor.lastProject = new ProjectRef();
        homeManager.homeDescriptor.lastProject.setName(context.name);
        homeManager.homeDescriptor.lastProject.setPath(context.path);
        homeManager.save();
        projectContext.dispose();
        projectContext.copyFrom(context);
        projectContext.loaded = true;
        Gdx.graphics.setTitle(constructWindowTitle());
        Mundus.postEvent(new ProjectChangedEvent());
        toolManager.setDefaultTool();
    }

    public boolean openLastOpenedProject() {
        ProjectRef lastOpenedProject = homeManager.getLastOpenedProject();
        if(lastOpenedProject != null) {
            ProjectContext context = loadProject(lastOpenedProject);
            if(new File(context.path).exists()) {
                changeProject(context);
                return true;
            } else {
                Log.error("Failed to load last opened project");
            }
        }

        return false;
    }

    public MModel importG3dbModel(ImportManager.ImportedModel importedModel) {
        long id = projectContext.obtainUUID();

        // copy to project's model folder
        String folder = projectContext.path + "/" + ProjectManager.PROJECT_MODEL_DIR + id + "/";
        FileHandle finalG3db = Gdx.files.absolute(folder + id + ".g3db");
        importedModel.g3dbFile.copyTo(finalG3db);
        importedModel.textureFile.copyTo(Gdx.files.absolute(folder));

        // load model
        G3dModelLoader loader = new G3dModelLoader(new UBJsonReader());
        Model model = loader.loadModel(finalG3db);

        // create persistable model
        MModel mModel = new MModel();
        mModel.setModel(model);
        mModel.name = importedModel.name;
        mModel.id = id;
        mModel.g3dbPath = finalG3db.path();
        mModel.texturePath = FilenameUtils.concat(Gdx.files.absolute(folder).path(), importedModel.textureFile.name());
        System.out.println(finalG3db);
        projectContext.models.add(mModel);

        // save whole project
        saveProject(projectContext);

        return mModel;
    }

    public MTexture importTexture(String name, FileHandle textureFile) {
        long id = projectContext.obtainUUID();

        // copy file
        String internalName = id + "." + textureFile.extension();
        String importPath = FilenameUtils.concat(projectContext.path, PROJECT_TEXTURE_DIR + internalName);
        textureFile.copyTo(Gdx.files.absolute(importPath));

        MTexture tex = new MTexture();
        tex.setId(id);
        tex.setName(name);
        tex.texture = new Texture(textureFile);
        tex.path = PROJECT_TEXTURE_DIR + internalName;
        projectContext.textures.add(tex);

        // save whole project
        saveProject(projectContext);

        return tex;
    }

    public void saveProject(ProjectContext projectContext) {
        // save terrain data in .terra files
        for(Terrain terrain : projectContext.terrains) {
            String path = FilenameUtils.concat(projectContext.path, ProjectManager.PROJECT_TERRAIN_DIR);
            path += terrain.id + "." + TerrainIO.FILE_EXTENSION;
            terrain.terraPath = path;
            TerrainIO.exportTerrain(terrain, path);
        }

        // save context in .pro file
        kryoManager.saveProjectContext(projectContext);

        Log.debug("Saving project " + projectContext.name+ " [" + projectContext.path + "]");
    }

    public Scene createScene(ProjectContext projectContext, String name) {
        Scene scene = new Scene();
        long id = projectContext.obtainUUID();
        scene.setId(id);
        scene.setName(name);
        projectContext.scenes.add(scene);

        return scene;
    }

    public void changeScene(Scene scene) {
        toolManager.deactivateTool();
        projectContext.currScene = scene;
        Gdx.graphics.setTitle(constructWindowTitle());
        Mundus.postEvent(new ProjectChangedEvent());
        toolManager.setDefaultTool();
    }

}
