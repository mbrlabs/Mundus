package com.mbrlabs.mundus.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mbrlabs.mundus.utils.Colors;
import com.mbrlabs.mundus.Mundus;
import com.mbrlabs.mundus.importer.FbxConv;
import com.mbrlabs.mundus.navigation.FreeCamController;
import com.mbrlabs.mundus.ui.components.MundusToolbar;
import com.mbrlabs.mundus.ui.components.menu.MundusMenuBar;
import com.mbrlabs.mundus.utils.GlUtils;
import com.mbrlabs.mundus.utils.UsefulMeshs;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class MainScreen extends BaseScreen {

    // UI stuff
    private Stage stage;
    private Table rootTable;
    private MundusMenuBar menuBar;
    private MundusToolbar toolbar;
    private FileChooser fileChooser;

    // axes
    public Model axesModel;
    public ModelInstance axesInstance;
    private boolean showAxes = true;

    // sample model
    private Model model;
    private ModelInstance modelInstance;

    // lights
    private Environment environment = new Environment();
    private PointLight light;
    // input
    private InputMultiplexer inputMultiplexer;

    private FreeCamController camController;

    private FileChooserAdapter fileChooserImportModel = new FCAdapterImportModel();

    private FbxConv fbxConv = new FbxConv();

    public MainScreen(final Mundus mundus) {
        super(mundus);
        setupUI();
        setupFileChooser();

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

        setupInput();
    }

    private void setupUI() {
        this.stage = new Stage(new ScreenViewport());

        // create root table
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.align(Align.left | Align.top);
       // rootTable.setDebug(true);
        stage.addActor(this.rootTable);

        // row 1: add menu
        menuBar = new MundusMenuBar();
        rootTable.add(menuBar.getTable()).fillX().expandX().row();

        // row 2: toolbar
        toolbar = new MundusToolbar();
        rootTable.add(toolbar).fillX().expandX().row();

        // row 3: content
        VisList<String> modelList = new VisList<String>();
        modelList.getStyle().background = VisUI.getSkin().getDrawable("default-pane");
        for(int i = 0; i < 90; i++) {
            modelList.getItems().add("Model " + i);
        }
        VisScrollPane scrollPane = new VisScrollPane(modelList);
        rootTable.add(scrollPane).width(300).top().left().expandY().fillY().row();

        // TODO row 4: status bar

    }

    private void setupFileChooser() {
        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
    }

    private void setupInput() {
        camController = new FreeCamController(mundus.cam);
        inputMultiplexer = new InputMultiplexer();
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

        inputMultiplexer.addProcessor(stage);
        toolbar.getImportBtn().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fileChooser.setListener(fileChooserImportModel);
                stage.addActor(fileChooser.fadeIn());
            }
        });

        Gdx.input.setInputProcessor(inputMultiplexer);
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

    /**
     *
     */
    private class FCAdapterImportModel extends FileChooserAdapter {
        @Override
        public void selected(FileHandle file) {
            String pathToFile = file.path();
            String outputPath = FilenameUtils.getFullPath(pathToFile);

            fbxConv.clear();
            fbxConv.input(pathToFile).output(outputPath).flipTexture(true).outputFormat(FbxConv.OUTPUT_FORMAT_G3DJ);
            fbxConv.execute(result -> {
                System.out.println("Import result: " + result.isSuccess());
                System.out.println("Import log: " + result.getLog());
            });

        }
    }

}
