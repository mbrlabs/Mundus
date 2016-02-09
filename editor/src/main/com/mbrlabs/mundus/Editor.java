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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.SceneChangedEvent;
import com.mbrlabs.mundus.input.FreeCamController;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.widgets.Actor3D;
import com.mbrlabs.mundus.utils.Compass;
import com.mbrlabs.mundus.utils.GlUtils;
import com.mbrlabs.mundus.utils.TestUtils;
import com.mbrlabs.mundus.utils.UsefulMeshs;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Editor implements ApplicationListener, ProjectChangedEvent.ProjectChangedListener, SceneChangedEvent.SceneChangedListener {

    private ModelInstance axesInstance;

    private Ui ui;
    private Compass compass;
    private ModelBatch batch;

    private Actor3D actor3D;

    @Inject
    private FreeCamController camController;
    @Inject
    private InputManager inputManager;
    @Inject
    private ProjectContext projectContext;
    @Inject
    private Shaders shaders;
    @Inject
    private ProjectManager projectManager;
    @Inject
    private HomeManager homeManager;
    @Inject
    private ToolManager toolManager;

	@Override
	public void create () {
        Mundus.init();
        Mundus.registerEventListener(this);
        Mundus.inject(this);
        batch = Mundus.modelBatch;
        ui = Ui.getInstance();

        Model axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);
        Mundus.testModels.add(axesModel);

        actor3D =  Ui.getInstance().getActor3D();
        actor3D.setCam(projectContext.currScene.cam);
        actor3D.setRenderer(new Actor3D.Renderer() {
            @Override
            public void render(Camera cam) {
                if(projectContext.currScene.skybox != null) {
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
        boolean projectOpened = projectManager.openLastOpenedProject();
        if(!projectOpened) {
            createDefaultProject();
        }

        compass = new Compass(projectContext.currScene.cam);
        camController.setCamera(projectContext.currScene.cam);
        setupInput();
    }

    private void setupInput() {
        // NOTE: order in wich processors are added is important: first added, first executed!
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
	public void render () {
        GlUtils.clearScreen(Color.WHITE);
        ui.act();
        camController.update();
        toolManager.act();

        //projectContext.currScene.cam.update();
        ui.draw();
	}

    private void resetCam() {
        if(compass != null) {
            compass.setWorldCam(projectContext.currScene.cam);
        }
        if(camController != null) {
            camController.setCamera(projectContext.currScene.cam);
        }
        if(actor3D != null) {
            actor3D.setCam(projectContext.currScene.cam);
            projectContext.currScene.viewport = actor3D.getViewport();
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

    @Deprecated
    private void createTestModels() {
        // boxes to test terrain height
        if(projectContext.currScene.terrainGroup.size() > 0) {
            float boxSize = 0.5f;
            Model boxModel = new ModelBuilder().createBox(boxSize, boxSize,boxSize,
                    new Material(ColorAttribute.createDiffuse(Color.RED)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            Mundus.testModels.add(boxModel);
            Mundus.testInstances.addAll(TestUtils.createABunchOfModelsOnTheTerrain(1000,
                    boxModel, projectContext.currScene.terrainGroup.first()));
        }
    }

    private void createDefaultProject() {
        if(homeManager.homeDescriptor.lastProject == null || homeManager.homeDescriptor.projects.size() == 0) {
            String name = "Default Project";
            String path = FileUtils.getUserDirectoryPath();
            path = FilenameUtils.concat(path, "MundusProjects");

            ProjectContext project = projectManager.createProject(name, path);
            projectManager.saveProject(project);
            projectManager.changeProject(project);
        }
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
        System.out.println(width + " "  + height);
    }

    @Override
    public void dispose() {
        Mundus.dispose();
    }

}

