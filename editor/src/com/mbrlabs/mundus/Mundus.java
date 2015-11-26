package com.mbrlabs.mundus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.data.Bootstrap;
import com.mbrlabs.mundus.data.projects.ProjectManager;
import com.mbrlabs.mundus.data.settings.SettingsManager;
import com.mbrlabs.mundus.data.projects.Project;
import com.mbrlabs.mundus.shader.EntityShader;
import com.mbrlabs.mundus.shader.TerrainShader;
import com.mbrlabs.mundus.ui.screens.MainScreen;

import java.util.Date;

public class Mundus extends Game {

    // render stuff
    public TerrainShader terrainShader;
    public EntityShader entityShader;
    public ModelBatch modelBatch;

    // cam
    public PerspectiveCamera cam;

	@Override
	public void create () {
        Bootstrap.bootstrap();
        init();
        setScreen(new MainScreen(this));
	}

    private void init() {
        VisUI.load();
        FileChooser.setFavoritesPrefsName(Mundus.class.getPackage().getName());
        // cam
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, 3);
        cam.lookAt(0,1,1);
        cam.near = 0.2f;
        cam.far = 300f;
        cam.update();

        // shaders
        ShaderProgram.pedantic = false;
        terrainShader = new TerrainShader();
        terrainShader.init();
        entityShader = new EntityShader();
        entityShader.init();

        modelBatch = new ModelBatch();

        
    }

	@Override
	public void render () {
        super.render();
	}

    @Override
    public void dispose() {
        super.dispose();
        terrainShader.dispose();
        entityShader.dispose();
        modelBatch.dispose();
        VisUI.dispose();
    }


}
