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

package com.mbrlabs.mundus;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.core.registry.Registry;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.SceneChangedEvent;
import com.mbrlabs.mundus.input.FreeCamController;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.input.ShortcutController;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.widgets.RenderWidget;
import com.mbrlabs.mundus.utils.Compass;
import com.mbrlabs.mundus.utils.GlUtils;
import com.mbrlabs.mundus.utils.UsefulMeshs;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Marcus Brummer
 * @version 07-06-2016
 */
public class Editor implements ApplicationListener, ProjectChangedEvent.ProjectChangedListener,
        SceneChangedEvent.SceneChangedListener {

    private ModelInstance axesInstance;

    private Ui ui;
    private Compass compass;
    private ModelBatch batch;

    private RenderWidget widget3D;

    @Inject
    private FreeCamController camController;
    @Inject
    private ShortcutController shortcutController;
    @Inject
    private InputManager inputManager;
    @Inject
    private Shaders shaders;
    @Inject
    private ProjectManager projectManager;
    @Inject
    private Registry registry;
    @Inject
    private ToolManager toolManager;
    @Inject
    private GameObjectPicker goPicker;

    @Override
    public void create() {
        Mundus.init();
        Mundus.registerEventListener(this);
        Mundus.inject(this);
        batch = Mundus.modelBatch;
        ui = Ui.getInstance();

        // TODO dispose this
        Model axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);

        final ProjectContext projectContext = projectManager.current();

        widget3D = Ui.getInstance().getWidget3D();
        widget3D.setCam(projectContext.currScene.cam);
        widget3D.setRenderer(new RenderWidget.Renderer() {
            @Override
            public void render(Camera cam) {
                if (projectContext.currScene.skybox != null) {
                    batch.begin(projectContext.currScene.cam);
                    batch.render(projectContext.currScene.skybox.getSkyboxInstance(),
                            projectContext.currScene.environment, shaders.skyboxShader);
                    batch.end();
                }

                projectContext.currScene.sceneGraph.update();
                projectContext.currScene.sceneGraph.render();

                toolManager.render();
                compass.render(batch);
            }
        });

        // open last edited project or create default project
        ProjectContext context = projectManager.loadLastProject();
        if (context == null) {
            context = createDefaultProject();
        }

        projectManager.changeProject(context);
        compass = new Compass(projectContext.currScene.cam);
        camController.setCamera(projectContext.currScene.cam);
        setupInput();

        setupCloseListener();

        // for(FileHandle file : new
        // FileHandle("/home/marcus/Desktop/testAssets/").list()) {
        // context.assetManager.importAsset(file, TextureAsset.class);
        // }
        //
        // try {
        // AssetHelper.createTerrainAsset(new
        // FileHandle("/home/marcus/MundusProjects/Default Project/assets"),
        // 180);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        //
        // try {
        // AssetHelper.createPixmapTextureAsset(new
        // FileHandle("/home/marcus/MundusProjects/Default Project/assets"),
        // 512);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    private void setupCloseListener() {
        Main.closeListener = new Main.WindowCloseListener() {
            @Override
            public boolean onCloseRequested() {
//                Dialogs.showOptionDialog(Ui.getInstance(), "Confirm exit", "Do you really want to close Mundus?",
//                    Dialogs.OptionDialogType.YES_NO, new OptionDialogAdapter() {
//                        @Override
//                        public void yes() {
//                            Gdx.app.exit();
//                        }
//
//                        @Override
//                        public void no() {
//                            super.no();
//                        }
//                    });
                Ui ui = Ui.getInstance();
                ui.showDialog(ui.getExitDialog());
                return true;
            }
        };
    }

    private void setupInput() {
        // NOTE: order in wich processors are added is important: first added,
        // first executed!
        inputManager.addProcessor(shortcutController);
        inputManager.addProcessor(ui);
        // when user does not click on a ui element -> unfocus UI
        inputManager.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                ui.unfocusAll();
                return false;
            }
        });
        inputManager.addProcessor(toolManager);
        inputManager.addProcessor(camController);
        toolManager.setDefaultTool();
    }

    @Override
    public void render() {
        GlUtils.clearScreen(Color.WHITE);
        ui.act();
        camController.update();
        toolManager.act();
        ui.draw();
    }

    private void resetCam() {
        final ProjectContext projectContext = projectManager.current();
        if (compass != null) {
            compass.setWorldCam(projectContext.currScene.cam);
        }
        if (camController != null) {
            camController.setCamera(projectContext.currScene.cam);
        }
        if (widget3D != null) {
            widget3D.setCam(projectContext.currScene.cam);
            projectContext.currScene.viewport = widget3D.getViewport();
        }
    }

    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        resetCam();
    }

    @Override
    public void onSceneChanged(SceneChangedEvent sceneChangedEvent) {
        resetCam();
    }

    private ProjectContext createDefaultProject() {
        if (registry.getLastOpenedProject() == null || registry.getProjects().size() == 0) {
            String name = "Default Project";
            String path = FileUtils.getUserDirectoryPath();
            path = FilenameUtils.concat(path, "MundusProjects");

            ProjectContext project = projectManager.createProject(name, path);
            projectManager.saveProject(project);
            return project;
        }

        return null;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {
        ui.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        Mundus.dispose();
    }

}
