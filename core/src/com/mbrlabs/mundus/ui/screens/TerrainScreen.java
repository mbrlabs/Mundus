package com.mbrlabs.mundus.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
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
public class TerrainScreen extends BaseScreen {

    private Stage stage;
    private VisTable root;

    private MundusMenuBar menuBar;

    private Model model;
    private ModelInstance modelInstance;

    private PointLight light;

    private InputMultiplexer inputMultiplexer;
    private CameraInputController camController;

    ModelBatch modelBatch = new ModelBatch();

    Environment environment = new Environment();


    public TerrainScreen(Mundus mundus) {
        super(mundus);
        setupStage();

        // Menu
        menuBar = new MundusMenuBar();
        root.add(menuBar.getTable()).fillX().expandX().row();

        ModelLoader modelLoader = new ObjLoader();
        model = modelLoader.loadModel(Gdx.files.internal("ship/ship.obj"));
        modelInstance = new ModelInstance(model);

        light = new PointLight();
        light.setPosition(0,10,-10);
        light.setIntensity(5);
        environment.add(light);

        camController = new CameraInputController(mundus.cam);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(camController);



        Gdx.input.setInputProcessor(inputMultiplexer);
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

        modelBatch.begin(mundus.cam);
        modelBatch.render(modelInstance, environment, mundus.terrainShader);
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
        stage.dispose();
    }

}
