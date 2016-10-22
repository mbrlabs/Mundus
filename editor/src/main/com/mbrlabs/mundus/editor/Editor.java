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

package com.mbrlabs.mundus.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.mbrlabs.mundus.editor.core.Inject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.input.FreeCamController;
import com.mbrlabs.mundus.editor.input.InputManager;
import com.mbrlabs.mundus.editor.input.ShortcutController;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.ui.Ui;
import com.mbrlabs.mundus.editor.ui.widgets.RenderWidget;
import com.mbrlabs.mundus.editor.utils.Compass;
import com.mbrlabs.mundus.editor.utils.GlUtils;
import com.mbrlabs.mundus.editor.utils.UsefulMeshs;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Marcus Brummer
 * @version 07-06-2016
 */
public class Editor extends Lwjgl3WindowAdapter implements ApplicationListener,
        ProjectChangedEvent.ProjectChangedListener, SceneChangedEvent.SceneChangedListener {

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
    private ProjectManager projectManager;
    @Inject
    private Registry registry;
    @Inject
    private ToolManager toolManager;
    @Inject
    private GameObjectPicker goPicker;

    @Override
    public void create() {
        Mundus.setAppIcon();
        Mundus.init();
        Mundus.registerEventListener(this);
        Mundus.inject(this);

        batch = Mundus.modelBatch;
        ui = Ui.getInstance();
        setupInput();

        // TODO dispose this
        Model axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);

        // open last edited project or create default project
        ProjectContext context = projectManager.loadLastProject();
        if (context == null) {
            context = createDefaultProject();
        }

        // setup render widget
        widget3D = Ui.getInstance().getWidget3D();
        compass = new Compass(context.currScene.cam);

        // change project; this will fire a ProjectChangedEvent
        projectManager.changeProject(context);

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

    private void setupSceneWidget() {
        final ProjectContext context = projectManager.current();
        widget3D.setCam(context.currScene.cam);
        widget3D.setRenderer(cam -> {
            if (context.currScene.skybox != null) {
                batch.begin(context.currScene.cam);
                batch.render(context.currScene.skybox.getSkyboxInstance(), context.currScene.environment,
                        Shaders.INSTANCE.getSkyboxShader());
                batch.end();
            }

            context.currScene.sceneGraph.update();
            context.currScene.sceneGraph.render();

            toolManager.render();
            compass.render(batch);
        });

        compass.setWorldCam(context.currScene.cam);
        camController.setCamera(context.currScene.cam);
        widget3D.setCam(context.currScene.cam);
        context.currScene.viewport = widget3D.getViewport();
    }

    @Override
    public void render() {
        GlUtils.clearScreen(Color.WHITE);
        ui.act();
        camController.update();
        toolManager.act();
        ui.draw();
    }

    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        setupSceneWidget();
    }

    @Override
    public void onSceneChanged(SceneChangedEvent sceneChangedEvent) {
        setupSceneWidget();
    }

    private ProjectContext createDefaultProject() {
        if (registry.getLastOpenedProject() == null || registry.getProjects().size() == 0) {
            String name = "Default Project";
            String path = FileUtils.getUserDirectoryPath();
            path = FilenameUtils.concat(path, "MundusProjects");

            return projectManager.createProject(name, path);
        }

        return null;
    }

    @Override
    public boolean closeRequested() {
        Ui ui = Ui.getInstance();
        ui.showDialog(ui.getExitDialog());
        return false;
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
