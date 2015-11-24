package com.mbrlabs.mundus.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.Colors;
import com.mbrlabs.mundus.Mundus;
import com.mbrlabs.mundus.navigation.FreeCamController;
import com.mbrlabs.mundus.ui.components.EntityPerspectiveTable;
import com.mbrlabs.mundus.ui.components.menu.MundusMenuBar;
import com.mbrlabs.mundus.utils.GlUtils;
import com.mbrlabs.mundus.utils.UsefulMeshs;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class MainScreen extends BaseScreen {

    private Stage stage;
    private Table rootTable;
    private Table contentTable;
    private EntityPerspectiveTable entityTable;

    private MundusMenuBar menuBar;

    public Model axesModel;
    public ModelInstance axesInstance;
    private boolean showAxes = true;

    private Model model;
    private ModelInstance modelInstance;

    private PointLight light;

    private InputMultiplexer inputMultiplexer;
    private FreeCamController camController;

    private Environment environment = new Environment();

    public MainScreen(final Mundus mundus) {
        super(mundus);
        setupUI();


        ModelLoader modelLoader = new G3dModelLoader(new UBJsonReader());
        model = modelLoader.loadModel(Gdx.files.internal("models/ship/g3db/ship.g3db"));
        modelInstance = new ModelInstance(model);
        modelInstance.transform.translate(0, 0.7f, 0);
        axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);

        light = new PointLight();
        light.setPosition(0,10,-10);
        light.setIntensity(1);
        environment.add(light);

        camController = new FreeCamController(mundus.cam);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(camController);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.F1) {
                    mundus.entityShader.toggleWireframe();
                    System.out.println("sdf");
                }
                return true;
            }
        });



        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void setupUI() {
        this.stage = new Stage(new ScreenViewport());

        // root table
        rootTable = new Table();
        rootTable.setWidth(stage.getWidth());
        rootTable.align(Align.center | Align.top);
        rootTable.setPosition(0, Gdx.graphics.getHeight());
        rootTable.debugAll();

        // Menu
        menuBar = new MundusMenuBar();
        rootTable.add(menuBar.getTable()).fillX().expandX().row();

        // content table
        contentTable = new Table();
        rootTable.add(contentTable).fill().expand().right();

        // entity perspective table
        entityTable = new EntityPerspectiveTable();

        // set current perspective
        contentTable.add(entityTable).fill().expand().right().row();

        stage.addActor(this.rootTable);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        GlUtils.clearScreen(Colors.GRAY_222);

        // updates
        camController.update();
        stage.act(delta);

        // render axes
        if (showAxes) mundus.modelBatch.render(axesInstance);
        // render entities
        mundus.modelBatch.begin(mundus.cam);
        mundus.modelBatch.render(modelInstance, environment, mundus.entityShader);
        mundus.modelBatch.end();

        // TODO render terrains

        // render UI
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
    }

}
