package com.mbrlabs.mundus;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.core.project.ProjectRef;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.terrain.TerrainInstance;
import com.mbrlabs.mundus.terrain.brushes.BrushManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.shader.Shaders;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.input.navigation.FreeCamController;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.*;

public class Editor implements ApplicationListener {

    // axes
    private RenderContext renderContext;
    private ModelInstance axesInstance;

    private Ui ui;
    private Compass compass;
    private FreeCamController camController;

    @Inject
    private InputManager inputManager;
    @Inject
    private PerspectiveCamera cam;
    @Inject
    private ModelBatch batch;
    @Inject
    private BrushManager brushManager;
    @Inject
    private ProjectContext projectContext;
    @Inject
    private Shaders shaders;
    @Inject
    private ProjectManager projectManager;
    @Inject
    private HomeManager homeManager;
    @Inject
    private EventBus eventBus;

	@Override
	public void create () {
        Mundus.init();
        Mundus.inject(this);
        ui = Ui.getInstance();
        inputManager.addProcessor(ui);
        camController = new FreeCamController(cam);
        inputManager.addProcessor(camController);
        compass = new Compass(cam);


        Model axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);
        Mundus.testModels.add(axesModel);

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));

        createTestModels();

        openLastOpenedProject();

    }

	@Override
	public void render () {
        GlUtils.clearScreen(Color.WHITE);
        ui.act();
        camController.update();
        brushManager.act();

        // update status bar
        ui.getStatusBar().setFps(Gdx.graphics.getFramesPerSecond());
        ui.getStatusBar().setVertexCount(0);

        // render model instances
       batch.begin(cam);
       batch.render(axesInstance);
//       batch.render(projectContext.entities,
//                projectContext.environment, shaders.entityShader);
        batch.render(Mundus.testInstances,
                projectContext.environment, shaders.entityShader);
        batch.end();

        // render terrains
        shaders.terrainShader.begin(cam, renderContext);
        for(TerrainInstance terrain : projectContext.terrainGroup.getTerrains()) {
            terrain.terrain.renderable.environment = projectContext.environment;
            terrain.terrain.renderable.worldTransform.set(terrain.transform);
            shaders.terrainShader.render(terrain.terrain.renderable);
        }
        shaders.terrainShader.end();

        // render active brush
        if(brushManager.getActiveBrush() != null) {
            brushManager.getActiveBrush().render(cam, batch);
        }

        // render compass
        compass.render(batch);

        // render UI
        ui.draw();
	}

    @Deprecated
    private void createTestModels() {
        // boxes to test terrain height
        if(projectContext.terrainGroup.size() > 0) {
            float boxSize = 0.5f;
            Model boxModel = new ModelBuilder().createBox(boxSize, boxSize,boxSize,
                    new Material(ColorAttribute.createDiffuse(Color.RED)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            Mundus.testModels.add(boxModel);
            Mundus.testInstances.addAll(TestUtils.createABunchOfModelsOnTheTerrain(1000,
                    boxModel, projectContext.terrainGroup.first()));
        }
    }

    private void openLastOpenedProject() {
        ProjectRef lastOpenedProject = homeManager.getLastOpenedProject();
        if(lastOpenedProject != null) {
            projectManager.loadProject(lastOpenedProject, new Callback<ProjectContext>() {
                @Override
                public void done(ProjectContext result) {
                    projectManager.changeProject(result);
                }

                @Override
                public void error(String msg) {
                    Log.error("Failed to load last opened project");
                }
            });
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
    }

    @Override
    public void dispose() {
        Mundus.dispose();
    }

}
