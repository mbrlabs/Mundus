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
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.components.StatusBar;
import com.mbrlabs.mundus.ui.components.dialogs.SettingsDialog;
import com.mbrlabs.mundus.utils.Colors;
import com.mbrlabs.mundus.Mundus;
import com.mbrlabs.mundus.importer.FbxConv;
import com.mbrlabs.mundus.navigation.FreeCamController;
import com.mbrlabs.mundus.ui.components.MundusToolbar;
import com.mbrlabs.mundus.ui.components.menu.MundusMenuBar;
import com.mbrlabs.mundus.utils.GlUtils;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.utils.UsefulMeshs;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class MainScreen extends BaseScreen {

    // UI stuff
    private Ui ui;

    // axes
    public Model axesModel;
    public ModelInstance axesInstance;
    private boolean showAxes = true;

    // sample model
    private Model model;
    private Array<ModelInstance> modelInstances = new Array<>();

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
        ui = Ui.getInstance();


        ModelLoader modelLoader = new G3dModelLoader(new UBJsonReader());
        model = modelLoader.loadModel(Gdx.files.internal("models/ship/g3db/ship.g3db"));
        ModelInstance modelInstance = new ModelInstance(model);
        modelInstance.transform.translate(0, 0.7f, 0);
        modelInstances.add(modelInstance);

        axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);

        light = new PointLight();
        light.setPosition(0,10,-10);
        light.setIntensity(1);
        environment.add(light);

        setupInput();
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

        inputMultiplexer.addProcessor(ui);
        ui.getToolbar().getImportBtn().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ui.getFileChooser().setListener(fileChooserImportModel);
                ui.addActor(ui.getFileChooser().fadeIn());
            }
        });

        ui.getMenuBar().getFileMenu().getNewProject().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("sd");
            }
        });

        ui.getMenuBar().getWindowMenu().getSettings().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ui.getSettingsDialog().show(ui);
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

        // ui updates
        ui.getStatusBar().setFps(Gdx.graphics.getFramesPerSecond());

        // updates
        mundus.cam.update();
        camController.update();
        ui.act(delta);

        // render axes
        if (showAxes) mundus.modelBatch.render(axesInstance);
        // render entities
        mundus.modelBatch.begin(mundus.cam);
        mundus.modelBatch.render(modelInstances, environment, mundus.entityShader);
        mundus.modelBatch.end();

        // TODO render terrains

        // render UI
        ui.draw();

    }

    @Override
    public void resize(int width, int height) {
        ui.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        this.ui.dispose();
        this.ui = null;
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
            fbxConv.input(pathToFile).output(outputPath).flipTexture(true).outputFormat(FbxConv.OUTPUT_FORMAT_G3DB);
            fbxConv.execute(result -> {
                Log.debug("Import result: " + result.isSuccess());
                Log.debug("Import log: " + result.getLog());
                Model model = new G3dModelLoader(new UBJsonReader()).loadModel(Gdx.files.absolute(result.getOutputFile()));
                ui.getModelList().getItems().add(model);
                modelInstances.add(new ModelInstance(ui.getModelList().getItems().first()));
                ui.getModelList().layout();
            });



        }
    }

}
