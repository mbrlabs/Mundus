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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mbrlabs.mundus.World;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.Colors;
import com.mbrlabs.mundus.Mundus;
import com.mbrlabs.mundus.importer.FbxConv;
import com.mbrlabs.mundus.input.navigation.FreeCamController;
import com.mbrlabs.mundus.utils.GlUtils;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.utils.UsefulMeshs;
import org.apache.commons.io.FilenameUtils;

import java.util.Random;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class MainScreen extends BaseScreen {

    private Ui ui;
    private World world;

    // input
    private InputMultiplexer inputMultiplexer;
    private FreeCamController camController;

    private long vertexCount = 0;


    public MainScreen(final Mundus mundus) {
        super(mundus);
        world = World.getInstance();
        ui = Ui.getInstance();

        setupInput();
    }

    private void setupInput() {
        camController = new FreeCamController(mundus.cam);
        inputMultiplexer = new InputMultiplexer();

        // 3 input processors: stage, free cam nav, F1, F2 keys...
        inputMultiplexer.addProcessor(camController);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.F1) {
                    mundus.entityShader.toggleWireframe();
                }
                if(keycode == Input.Keys.F2) {
                    if(world.models.size > 0) {
                        Random rand = new Random();
                        for(int i = 0; i < 200; i++) {
                            ModelInstance instance = new ModelInstance(world.models.first());

                            instance.transform.translate(rand.nextFloat() * 1000, 0, rand.nextFloat()*1000);
                            instance.transform.rotate(0, rand.nextFloat(), 0, rand.nextFloat()*360);
                            world.entities.add(instance);

                        }
                    }
                }
                return true;
            }
        });
        inputMultiplexer.addProcessor(ui);
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
        ui.getStatusBar().setVertexCount(vertexCount);
        ui.act(delta);

        camController.update();

        mundus.modelBatch.begin(mundus.cam);
        mundus.modelBatch.render(world.axesInstance);
        mundus.modelBatch.render(world.entities, world.environment, mundus.entityShader);
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
        this.world.dispose();
    }

}
