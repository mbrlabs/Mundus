package com.mbrlabs.mundus.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.Colors;
import com.mbrlabs.mundus.Mundus;
import com.mbrlabs.mundus.ui.components.menu.MundusMenuBar;
import com.mbrlabs.mundus.utils.GlUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class MainScreen extends BaseScreen {

    private Stage stage;
    private VisTable root;

    private MundusMenuBar menuBar;

    public Model axesModel;
    public ModelInstance axesInstance;
    private boolean showAxes = true;

    private Model model;
    private ModelInstance modelInstance;

    private PointLight light;

    private InputMultiplexer inputMultiplexer;
    private CameraInputController camController;

    private ModelBatch modelBatch = new ModelBatch();
    private Environment environment = new Environment();


    public MainScreen(final Mundus mundus) {
        super(mundus);
        setupStage();

        // Menu
        menuBar = new MundusMenuBar();
        root.add(menuBar.getTable()).fillX().expandX().row();

        ModelLoader modelLoader = new G3dModelLoader(new UBJsonReader());
        model = modelLoader.loadModel(Gdx.files.internal("ship/g3db/ship.g3db"));
        modelInstance = new ModelInstance(model);
        modelInstance.transform.translate(0,0.7f,0);
        createAxes();

        light = new PointLight();
        light.setPosition(0,10,-10);
        light.setIntensity(1);
        environment.add(light);

        camController = new CameraInputController(mundus.cam);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(camController);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.F1) {
                    mundus.entityShader.toggleWireframe();
                }
                return true;
            }
        });



        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void createAxes () {
        final float GRID_MIN = -10f;
        final float GRID_MAX = 10f;
        final float GRID_STEP = 1f;
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position
                | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
            builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
        }
        builder = modelBuilder.part("axes", GL20.GL_LINES, VertexAttributes.Usage.Position
                | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, 100, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, 0, 0, 0, 100, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, 0, 0, 0, 0, 100);
        axesModel = modelBuilder.end();
        axesInstance = new ModelInstance(axesModel);
    }

    private void setupStage() {
        this.stage = new Stage(new ScreenViewport());
        root = new VisTable();
        root.setWidth(stage.getWidth());
        root.align(Align.center | Align.top);
        root.setPosition(0, Gdx.graphics.getHeight());
        stage.addActor(this.root);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        GlUtils.clearScreen(Colors.GRAY_222);

        modelInstance.transform.rotate(0,1,0,1);

        if (showAxes) modelBatch.render(axesInstance);

        modelBatch.begin(mundus.cam);
        modelBatch.render(modelInstance, environment, mundus.entityShader);
        modelBatch.end();

        // stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        this.stage.dispose();
        this.stage = null;
        this.model.dispose();
        this.model = null;
        this.axesModel.dispose();
        this.axesModel = null;
        modelBatch.dispose();
    }

}
