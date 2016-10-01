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

package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.model.MModel;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.utils.TextureUtils;
import com.mbrlabs.mundus.core.EditorScene;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.kryo.DescriptorConverter;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.core.kryo.descriptors.SceneDescriptor;
import com.mbrlabs.mundus.core.registry.ProjectRef;
import com.mbrlabs.mundus.core.registry.Registry;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.SceneChangedEvent;
import com.mbrlabs.mundus.exceptions.ProjectAlreadyImportedException;
import com.mbrlabs.mundus.exceptions.ProjectOpenException;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.scene3d.components.PickableComponent;
import com.mbrlabs.mundus.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.utils.TerrainIO;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.utils.SkyboxBuilder;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Manages Mundus projects and scenes.
 *
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager implements Disposable {

    private static final String DEFAULT_SCENE_NAME = "Main Scene";

    public static final String PROJECT_ASSETS_DIR           =    "assets/";
    public static final String PROJECT_MODEL_DIR            =    PROJECT_ASSETS_DIR + "models/";
    public static final String PROJECT_TERRAIN_DIR          =    PROJECT_ASSETS_DIR + "terrains/";

    public static final String PROJECT_TEXTURE_DIR          =    PROJECT_ASSETS_DIR + "textures/";
    public static final String PROJECT_SCENES_DIR           =    "scenes/";

    public static final String PROJECT_SCENE_EXTENSION      =    ".mundus";

    private ProjectContext currentProject;
    private Registry registry;
    private KryoManager kryoManager;

    private Shaders shaders;

    public ProjectManager(KryoManager kryoManager, Registry registry, Shaders shaders) {
        this.registry = registry;
        this.kryoManager = kryoManager;
        this.shaders = shaders;
        currentProject = new ProjectContext(-1);
    }

    /**
     * Returns current project.
     *
     * @return  current project
     */
    public ProjectContext current() {
        return currentProject;
    }

    /**
     * Saves the active project
     */
    public void saveCurrentProject() {
        saveProject(currentProject);
    }

    public String assetFolder() {
        return currentProject.path + "/" + PROJECT_ASSETS_DIR;
    }

    /**
     * Creates & saves a new project.
     *
     * Creates a new project. However, it does not switch the current project.
     *
     * @param name      project name
     * @param folder    absolute path to project folder
     * @return          new project context
     */
    public ProjectContext createProject(String name, String folder) {
        ProjectRef ref = registry.createProjectRef(name, folder);
        String path = ref.getPath();
        new File(path).mkdirs();
        new File(path, PROJECT_MODEL_DIR).mkdirs();
        new File(path, PROJECT_TERRAIN_DIR).mkdirs();
        new File(path, PROJECT_TEXTURE_DIR).mkdirs();
        new File(path, PROJECT_SCENES_DIR).mkdirs();

        // create currentProject current
        ProjectContext newProjectContext = new ProjectContext(-1);
        newProjectContext.path = path;
        newProjectContext.name = ref.getName();

        // create default scene & save .mundus
        EditorScene scene = new EditorScene();
        scene.setName(DEFAULT_SCENE_NAME);
        scene.skybox = SkyboxBuilder.createDefaultSkybox();
        scene.environment.setFog(new Fog());
        scene.setId(newProjectContext.obtainID());
        kryoManager.saveScene(newProjectContext, scene);
        scene.sceneGraph.batch = Mundus.modelBatch;

        // save .pro file
        newProjectContext.scenes.add(scene.getName());
        newProjectContext.currScene = scene;
        saveProject(newProjectContext);

        return newProjectContext;
    }

    /**
     * Imports (opens) a mundus project, that is not in the registry.
     *
     * @param absolutePath                          path to project
     * @return                                      project context of imported project
     * @throws ProjectAlreadyImportedException      if project exists already in registry
     * @throws ProjectOpenException                 project could not be opened
     */
    public ProjectContext importProject(String absolutePath) throws ProjectAlreadyImportedException, ProjectOpenException {
        // check if already imported
        for (ProjectRef ref : registry.getProjects()) {
            if (ref.getPath().equals(absolutePath)) {
                throw new ProjectAlreadyImportedException("Project " + absolutePath + " is already imported");
            }
        }

        ProjectRef ref = new ProjectRef();
        ref.setPath(absolutePath);

        try {
            ProjectContext context = loadProject(ref);
            ref.setName(context.name);
            registry.getProjects().add(ref);
            kryoManager.saveRegistry(registry);
            return context;
        } catch (Exception e) {
            throw new ProjectOpenException(e.getMessage());
        }
    }

    /**
     * Loads the project context for a project.
     *
     * This does not open to that project, it only loads it.
     *
     * @param ref                       project reference to the project
     * @return                          loaded project context
     * @throws FileNotFoundException    if project can't be found
     */
    public ProjectContext loadProject(ProjectRef ref) throws FileNotFoundException {
        ProjectContext context = kryoManager.loadProjectContext(ref);
        context.path = ref.getPath();

        // load textures
        for(MTexture tex : context.textures) {
            tex.texture = TextureUtils.loadMipmapTexture(
                    Gdx.files.absolute(FilenameUtils.concat(context.path, tex.getPath())), true);
            Log.debug("Loaded texture: {}", tex.getPath());
        }

        // load g3db models
        G3dModelLoader loader = new G3dModelLoader(new UBJsonReader());
        for(MModel model : context.models) {
            model.setModel(loader.loadModel(Gdx.files.absolute(
                    FilenameUtils.concat(context.path, model.g3dbPath))));
        }

        // load terrain .terra files
        for(Terrain terrain : context.terrains) {
            TerrainIO.importTerrain(context, terrain);
        }

        context.currScene = loadScene(context, context.activeSceneName);

        return context;
    }

    /**
     * Completely saves a project & all scenes.
     *
     * @param projectContext    project context
     */
    public void saveProject(ProjectContext projectContext) {
        // save .terra files & the splat map
        for(Terrain terrain : projectContext.terrains) {
            TerrainIO.exportTerrain(projectContext, terrain);
        }

        // save current in .pro file
        kryoManager.saveProjectContext(projectContext);
        // save scene in .mundus file
        kryoManager.saveScene(projectContext, projectContext.currScene);

        Log.debug("Saving currentProject {}", projectContext.name+ " [" + projectContext.path + "]");
    }

    /**
     * Loads the project that was open when the user quit the program.
     *
     * Does not open open the project.
     *
     * @return      project context of last project
     */
    public ProjectContext loadLastProject() {
        ProjectRef lastOpenedProject = registry.getLastOpenedProject();
        if (lastOpenedProject != null) {
            try {
                return loadProject(lastOpenedProject);
            } catch (FileNotFoundException fnf) {
                Log.error(fnf.getMessage());
                fnf.printStackTrace();
                return null;
            }
        }

        return null;
    }

    /**
     * Opens a project.
     *
     * Opens a project. If a project is already open it will be disposed.
     * @param context   project context to open
     */
    public void changeProject(ProjectContext context) {
        if(currentProject != null) {
            currentProject.dispose();
        }

        currentProject.copyFrom(context);
        registry.setLastProject(new ProjectRef());
        registry.getLastOpenedProject().setName(context.name);
        registry.getLastOpenedProject().setPath(context.path);

        kryoManager.saveRegistry(registry);

        Gdx.graphics.setTitle(constructWindowTitle());
        Mundus.postEvent(new ProjectChangedEvent());
    }

    /**
     * Creates a new scene for the given project.
     *
     * @param project   project
     * @param name      scene name
     * @return          newly created scene
     */
    public Scene createScene(ProjectContext project, String name) {
        Scene scene = new Scene();
        long id = project.obtainID();
        scene.setId(id);
        scene.setName(name);
        scene.skybox = SkyboxBuilder.createDefaultSkybox();
        project.scenes.add(scene.getName());
        kryoManager.saveScene(project, scene);

        return scene;
    }

    /**
     * Loads a scene.
     *
     * This does not open the scene.
     *
     * @param context                   project context of the scene
     * @param sceneName                 name of the scene
     * @return                          loaded scene
     * @throws FileNotFoundException    if scene file not found
     */
    public EditorScene loadScene(ProjectContext context, String sceneName) throws FileNotFoundException {
        SceneDescriptor descriptor = kryoManager.loadScene(context, sceneName);
        EditorScene scene = DescriptorConverter.convert(descriptor, context.terrains, context.models);
        scene.skybox = SkyboxBuilder.createDefaultSkybox();

        SceneGraph sceneGraph = scene.sceneGraph;
        sceneGraph.batch = Mundus.modelBatch;
        for(GameObject go : sceneGraph.getGameObjects()) {
            initGameObject(context, go);
        }

        // create TerrainGroup for active scene
        Array<Component> terrainComponents = new Array<>();
        for(GameObject go : sceneGraph.getGameObjects()) {
            go.findComponentsByType(terrainComponents, Component.Type.TERRAIN, true);
        }
        for(Component c : terrainComponents) {
            if(c instanceof TerrainComponent) {
                scene.terrains.add(((TerrainComponent) c).getTerrain());
            }
        }

        return scene;
    }

    /**
     * Loads and opens scene
     *
     * @param projectContext    project context of scene
     * @param sceneName         scene name
     */
    public void changeScene(ProjectContext projectContext, String sceneName) {
        try {
            EditorScene newScene = loadScene(projectContext, sceneName);
            projectContext.currScene.dispose();
            projectContext.currScene = newScene;

            Gdx.graphics.setTitle(constructWindowTitle());
            Mundus.postEvent(new SceneChangedEvent());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.error(e.getMessage());
        }
    }

    private void initGameObject(ProjectContext context, GameObject root) {
        initComponents(context, root);
        if(root.getChildren() != null) {
            for(GameObject c : root.getChildren()) {
                initGameObject(context, c);
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
                    modelComponent.getModelInstance().modelInstance.transform = go.getTransform();
                    modelComponent.setShader(shaders.entityShader);
                } else {
                    Log.fatal("model for modelInstance not found: {}", modelComponent.getModelInstance().getModel().id);
                }
            } else if(c.getType() == Component.Type.TERRAIN) {
                ((TerrainComponent)c).setShader(shaders.terrainShader);
                ((TerrainComponent)c).getTerrain().setTransform(go.getTransform());
            }

            // encode id for picking
            if(c instanceof PickableComponent) {
                ((PickableComponent) c).encodeRaypickColorId();
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

    private String constructWindowTitle() {
        return currentProject.name + " - " + currentProject.currScene.getName() +
                " [" + currentProject.path +"]" + " - " + Main.TITLE;
    }

    @Override
    public void dispose() {
        currentProject.dispose();
    }
}
