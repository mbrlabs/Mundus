package com.mbrlabs.mundus;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.utils.UBJsonReader;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.data.ProjectContext;
import com.mbrlabs.mundus.data.home.MundusHome;
import com.mbrlabs.mundus.data.ProjectManager;
import com.mbrlabs.mundus.input.navigation.FreeCamController;
import com.mbrlabs.mundus.shader.EntityShader;
import com.mbrlabs.mundus.shader.TerrainShader;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.UiImages;
import com.mbrlabs.mundus.utils.Colors;
import com.mbrlabs.mundus.utils.GlUtils;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.utils.UsefulMeshs;

import java.util.Random;

public class Mundus implements ApplicationListener {

    // render stuff
    public TerrainShader terrainShader;
    public EntityShader entityShader;
    public ModelBatch modelBatch;
    public PerspectiveCamera cam;

    private Ui ui;
    public static ProjectContext context;

    // axes
    public Model axesModel;
    public ModelInstance axesInstance;


    // input
    private InputMultiplexer inputMultiplexer;
    private FreeCamController camController;

    private long vertexCount = 0;

	@Override
	public void create () {
        MundusHome.bootstrap();
        Log.init();
        init();

        context = new ProjectContext();
        ui = Ui.getInstance();

        axesModel = UsefulMeshs.createAxes();
        axesInstance = new ModelInstance(axesModel);

        setupInput();
	}

    private void init() {

        VisUI.load();
        UiImages.load();
        FileChooser.setFavoritesPrefsName(Mundus.class.getPackage().getName());
        // cam
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        cam.position.set(0, 1, 3);
        cam.lookAt(0,1,1);
        cam.near = 0.2f;
        cam.far = 3000f;
        cam.update();

        // shaders
        ShaderProgram.pedantic = false;
        terrainShader = new TerrainShader();
        terrainShader.init();
        entityShader = new EntityShader();
        entityShader.init();

        modelBatch = new ModelBatch();

        if(MundusHome.getInstance().getProjectRefs().getProjects().size() == 0) {
            ProjectManager.createProject("Skyrim", "/home/marcus/MundusProjects");
        }

    }

    private void setupInput() {
        camController = new FreeCamController(cam);
        inputMultiplexer = new InputMultiplexer();

        // 3 input processors: stage, free cam nav, F1, F2 keys...
        inputMultiplexer.addProcessor(camController);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.F1) {
                    //entityShader.toggleWireframe();

                }
                if(keycode == Input.Keys.F2) {
                    if(context.models.size > 0) {
                        Random rand = new Random();
                        for(int i = 0; i < 200; i++) {
                            ModelInstance instance = new ModelInstance(context.models.first());

                            instance.transform.translate(rand.nextFloat() * 1000, 0, rand.nextFloat()*1000);
                            instance.transform.rotate(0, rand.nextFloat(), 0, rand.nextFloat()*360);
                            context.entities.add(instance);

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
	public void render () {
        GlUtils.clearScreen(Colors.GRAY_222);

        // ui updates
        ui.getStatusBar().setFps(Gdx.graphics.getFramesPerSecond());
        ui.getStatusBar().setVertexCount(vertexCount);
        ui.act(Gdx.graphics.getDeltaTime());

        camController.update();

        modelBatch.begin(cam);
        modelBatch.render(axesInstance);
        modelBatch.render(context.entities, context.environment, entityShader);
        modelBatch.end();

        // TODO render terrains

        // render UI
        ui.draw();
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
        context.dispose();

        terrainShader.dispose();
        entityShader.dispose();
        modelBatch.dispose();
        VisUI.dispose();

    }


}
