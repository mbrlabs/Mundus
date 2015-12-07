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
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.data.ProjectContext;
import com.mbrlabs.mundus.data.home.MundusHome;
import com.mbrlabs.mundus.data.ProjectManager;
import com.mbrlabs.mundus.input.InputManager;
import com.mbrlabs.mundus.input.navigation.FreeCamController;
import com.mbrlabs.mundus.shader.BrushShader;
import com.mbrlabs.mundus.shader.EntityShader;
import com.mbrlabs.mundus.shader.TerrainShader;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.terrain.TerrainTest;
import com.mbrlabs.mundus.terrain.brushes.SphereBrush;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.UiImages;
import com.mbrlabs.mundus.utils.*;

import java.util.Random;

public class Mundus implements ApplicationListener {

    // render stuff
    public TerrainShader terrainShader;
    public EntityShader entityShader;
    public BrushShader brushShader;

    public ModelBatch modelBatch;
    public static PerspectiveCamera cam;

    private Ui ui;
    public static ProjectContext projectContext;

    private InputManager inputManager;

    // axes
    public Model axesModel;
    public ModelInstance axesInstance;

    // compass
    private Compass compass;

    private long vertexCount = 0;
    private RenderContext renderContext;
    private SphereBrush brush;

    private Model boxModel;

    private Vector3 tempV3 = new Vector3();

	@Override
	public void create () {
        MundusHome.bootstrap();
        Log.init();
        init();

        projectContext = new ProjectContext();
        ui = Ui.getInstance();

        axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);

        setupInput();

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
        projectContext.terrains.add(new TerrainTest().terrain);

        createBoxesOnTerrain();
    }

    private void init() {
        VisUI.load();
        UiImages.load();
        FileChooser.setFavoritesPrefsName(Mundus.class.getPackage().getName());
        // cam
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        cam.position.set(0, 1, -3);
        cam.lookAt(0,1,-1);
        cam.near = 0.2f;
        cam.far = 10000;
        cam.update();

        // shaders
        ShaderProgram.pedantic = false;
        terrainShader = new TerrainShader();
        terrainShader.init();
        entityShader = new EntityShader();
        entityShader.init();
        brushShader = new BrushShader();
        brushShader.init();

        modelBatch = new ModelBatch();

        brush = new SphereBrush();
        compass = new Compass(cam);

        if(MundusHome.getInstance().getProjectRefs().getProjects().size() == 0) {
            ProjectManager.createProject("Skyrim", "/home/marcus/MundusProjects");
        }

    }

    private void setupInput() {
        inputManager = new InputManager(ui);
        inputManager.setWorldNavigation(new FreeCamController(cam));
        inputManager.setCurrentToolInput(brush.getInputProcessor());
    }

	@Override
	public void render () {
        GlUtils.clearScreen(Colors.GRAY_222);

        inputManager.update();

        // update status bar
        ui.getStatusBar().setFps(Gdx.graphics.getFramesPerSecond());
        ui.getStatusBar().setVertexCount(vertexCount);

        // render model instances
        modelBatch.begin(cam);
        modelBatch.render(axesInstance);
        modelBatch.render(projectContext.entities, projectContext.environment, entityShader);
        modelBatch.end();

        // render terrain
        terrainShader.begin(cam, renderContext);
        for(Terrain terrain : projectContext.terrains) {
            terrain.renderable.environment = projectContext.environment;
            terrainShader.render(terrain.renderable);
        }
        terrainShader.end();

        // render brush
        modelBatch.begin(cam);
        modelBatch.render(brush.getRenderable(), brushShader);
        modelBatch.end();

        compass.render(modelBatch);


        // render UI
        ui.draw();
	}

    // do not use..just for testing height calculation of terrain
    @Deprecated
    private void createBoxesOnTerrain() {
        if(projectContext.terrains.first() != null) {
            float boxSize = 0.5f;
            boxModel = new ModelBuilder().createBox(boxSize, boxSize,boxSize, new Material(ColorAttribute.createDiffuse(Color.RED)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            projectContext.entities.addAll(TestUtils.createABunchOfModelsOnTheTerrain(10000, boxModel, projectContext.terrains.first()));
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
        ui.dispose();
        ui = null;
        axesModel.dispose();
        axesModel = null;
        compass.dispose();
        projectContext.dispose();

        terrainShader.dispose();
        entityShader.dispose();
        brushShader.dispose();
        modelBatch.dispose();
        VisUI.dispose();
    }

}
