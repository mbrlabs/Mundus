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

package com.mbrlabs.mundus.editor.core.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.AssetNotFoundException;
import com.mbrlabs.mundus.commons.assets.MetaFileParseException;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.editor.Main;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.EditorScene;
import com.mbrlabs.mundus.editor.core.kryo.DescriptorConverter;
import com.mbrlabs.mundus.editor.core.kryo.KryoManager;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.SceneDescriptor;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.editor.scene3d.components.PickableComponent;
import com.mbrlabs.mundus.editor.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.utils.Log;
import com.mbrlabs.mundus.editor.utils.SkyboxBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Manages Mundus projects and scenes.
 *
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager implements Disposable {

    private static final String TAG = ProjectManager.class.getSimpleName();

    private static final String DEFAULT_SCENE_NAME = "Main Scene";
    public static final String PROJECT_ASSETS_DIR = "assets/";
    public static final String PROJECT_SCENES_DIR = "scenes/";
    public static final String PROJECT_SCENE_EXTENSION = ".mundus";

    private ProjectContext currentProject;
    private Registry registry;
    private KryoManager kryoManager;
    private ModelBatch modelBatch;

    public ProjectManager(KryoManager kryoManager, Registry registry, ModelBatch modelBatch) {
        this.registry = registry;
        this.kryoManager = kryoManager;
        this.modelBatch = modelBatch;
        currentProject = new ProjectContext(-1);
    }

    /**
     * Returns current project.
     *
     * @return current project
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
     * @param name
     *            project name
     * @param folder
     *            absolute path to project folder
     * @return new project context
     */
    public ProjectContext createProject(String name, String folder) {
        ProjectRef ref = registry.createProjectRef(name, folder);
        String path = ref.getPath();
        new File(path).mkdirs();
        new File(path, PROJECT_ASSETS_DIR).mkdirs();
        new File(path, PROJECT_SCENES_DIR).mkdirs();

        // create currentProject current
        ProjectContext newProjectContext = new ProjectContext(-1);
        newProjectContext.path = path;
        newProjectContext.name = ref.getName();
        newProjectContext.assetManager = new EditorAssetManager(
                new FileHandle(path + "/" + ProjectManager.PROJECT_ASSETS_DIR));

        // create default scene & save .mundus
        EditorScene scene = new EditorScene();
        scene.setName(DEFAULT_SCENE_NAME);
        scene.skybox = SkyboxBuilder.createDefaultSkybox();
        scene.environment.setFog(new Fog());
        scene.setId(newProjectContext.obtainID());
        kryoManager.saveScene(newProjectContext, scene);
        scene.sceneGraph.batch = modelBatch;

        // save .pro file
        newProjectContext.scenes.add(scene.getName());
        newProjectContext.currScene = scene;
        saveProject(newProjectContext);

        // create standard assets
        newProjectContext.assetManager.createStandardAssets();

        return newProjectContext;
    }

    /**
     * Imports (opens) a mundus project, that is not in the registry.
     *
     * @param absolutePath
     *            path to project
     * @return project context of imported project
     * @throws ProjectAlreadyImportedException
     *             if project exists already in registry
     * @throws ProjectOpenException
     *             project could not be opened
     */
    public ProjectContext importProject(String absolutePath)
            throws ProjectAlreadyImportedException, ProjectOpenException {
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
     * @param ref
     *            project reference to the project
     * @return loaded project context
     * @throws FileNotFoundException
     *             if project can't be found
     */
    public ProjectContext loadProject(ProjectRef ref)
            throws FileNotFoundException, MetaFileParseException, AssetNotFoundException {
        ProjectContext context = kryoManager.loadProjectContext(ref);
        context.path = ref.getPath();

        // load assets
        context.assetManager = new EditorAssetManager(
                new FileHandle(ref.getPath() + "/" + ProjectManager.PROJECT_ASSETS_DIR));
        context.assetManager.loadAssets(new AssetManager.AssetLoadingListener() {
            @Override
            public void onLoad(Asset asset, int progress, int assetCount) {
                Log.debug(TAG, "Loaded {} asset ({}/{})", asset.getMeta().getType(), progress, assetCount);
            }

            @Override
            public void onFinish(int assetCount) {
                Log.debug(TAG, "Finished loading {} assets", assetCount);
            }
        });

        context.currScene = loadScene(context, context.activeSceneName);

        return context;
    }

    /**
     * Completely saves a project & all scenes.
     *
     * @param projectContext
     *            project context
     */
    public void saveProject(ProjectContext projectContext) {
        // save modified assets
        EditorAssetManager assetManager = projectContext.assetManager;
        for (Asset asset : assetManager.getDirtyAssets()) {
            try {
                Log.debug(TAG, "Saving dirty asset: {}", asset);
                assetManager.saveAsset(asset);
            } catch (IOException e) {
                Log.exception(TAG, e);
            }
        }

        // save current in .pro file
        kryoManager.saveProjectContext(projectContext);
        // save scene in .mundus file
        kryoManager.saveScene(projectContext, projectContext.currScene);

        Log.debug(TAG, "Saving currentProject {}", projectContext.name + " [" + projectContext.path + "]");
    }

    /**
     * Loads the project that was open when the user quit the program.
     *
     * Does not open open the project.
     *
     * @return project context of last project
     */
    public ProjectContext loadLastProject() {
        ProjectRef lastOpenedProject = registry.getLastOpenedProject();
        if (lastOpenedProject != null) {
            try {
                return loadProject(lastOpenedProject);
            } catch (FileNotFoundException fnf) {
                Log.error(TAG, fnf.getMessage());
                fnf.printStackTrace();
            } catch (AssetNotFoundException anf) {
                Log.error(TAG, anf.getMessage());
            } catch (MetaFileParseException mfp) {
                Log.error(TAG, mfp.getMessage());
            }
            return null;
        }

        return null;
    }

    /**
     * Opens a project.
     *
     * Opens a project. If a project is already open it will be disposed.
     * 
     * @param context
     *            project context to open
     */
    public void changeProject(ProjectContext context) {
        if (currentProject != null) {
            currentProject.dispose();
        }

        currentProject = context;
        // currentProject.copyFrom(context);
        registry.setLastProject(new ProjectRef());
        registry.getLastOpenedProject().setName(context.name);
        registry.getLastOpenedProject().setPath(context.path);

        kryoManager.saveRegistry(registry);

        Gdx.graphics.setTitle(constructWindowTitle());
        Mundus.INSTANCE.postEvent(new ProjectChangedEvent(context));
    }

    /**
     * Creates a new scene for the given project.
     *
     * @param project
     *            project
     * @param name
     *            scene name
     * @return newly created scene
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
     * @param context
     *            project context of the scene
     * @param sceneName
     *            name of the scene
     * @return loaded scene
     * @throws FileNotFoundException
     *             if scene file not found
     */
    public EditorScene loadScene(ProjectContext context, String sceneName) throws FileNotFoundException {
        SceneDescriptor descriptor = kryoManager.loadScene(context, sceneName);

        EditorScene scene = DescriptorConverter.convert(descriptor, context.assetManager.getAssetMap());
        scene.skybox = SkyboxBuilder.createDefaultSkybox();

        SceneGraph sceneGraph = scene.sceneGraph;
        sceneGraph.batch = modelBatch;
        for (GameObject go : sceneGraph.getGameObjects()) {
            initGameObject(context, go);
        }

        // create TerrainGroup for active scene
        Array<Component> terrainComponents = new Array<>();
        for (GameObject go : sceneGraph.getGameObjects()) {
            go.findComponentsByType(terrainComponents, Component.Type.TERRAIN, true);
        }
        for (Component c : terrainComponents) {
            if (c instanceof TerrainComponent) {
                scene.terrains.add(((TerrainComponent) c).getTerrain());
            }
        }

        return scene;
    }

    /**
     * Loads and opens scene
     *
     * @param projectContext
     *            project context of scene
     * @param sceneName
     *            scene name
     */
    public void changeScene(ProjectContext projectContext, String sceneName) {
        try {
            EditorScene newScene = loadScene(projectContext, sceneName);
            projectContext.currScene.dispose();
            projectContext.currScene = newScene;

            Gdx.graphics.setTitle(constructWindowTitle());
            Mundus.INSTANCE.postEvent(new SceneChangedEvent());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.error(TAG, e.getMessage());
        }
    }

    private void initGameObject(ProjectContext context, GameObject root) {
        initComponents(context, root);
        if (root.getChildren() != null) {
            for (GameObject c : root.getChildren()) {
                initGameObject(context, c);
            }
        }
    }

    private void initComponents(ProjectContext context, GameObject go) {
        Array<ModelAsset> models = context.assetManager.getModelAssets();
        for (Component c : go.getComponents()) {
            // Model component
            if (c.getType() == Component.Type.MODEL) {
                ModelComponent modelComponent = (ModelComponent) c;
                ModelAsset model = findModelById(models, modelComponent.getModelAsset().getID());
                if (model != null) {
                    modelComponent.setModel(model, false);
                } else {
                    Log.fatal(TAG, "model for modelInstance not found: {}", modelComponent.getModelAsset().getID());
                }
            } else if (c.getType() == Component.Type.TERRAIN) {
                ((TerrainComponent) c).setShader(Shaders.INSTANCE.getTerrainShader());
                ((TerrainComponent) c).getTerrain().getTerrain().setTransform(go.getTransform());
            }

            // encode id for picking
            if (c instanceof PickableComponent) {
                ((PickableComponent) c).encodeRaypickColorId();
            }
        }
    }

    private ModelAsset findModelById(Array<ModelAsset> models, String id) {
        for (ModelAsset m : models) {
            if (m.getID().equals(id)) {
                return m;
            }
        }

        return null;
    }

    private String constructWindowTitle() {
        return currentProject.name + " - " + currentProject.currScene.getName() + " [" + currentProject.path + "]"
                + " - " + Main.TITLE;
    }

    @Override
    public void dispose() {
        currentProject.dispose();
    }
}
