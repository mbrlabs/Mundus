package com.mbrlabs.mundus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.ui.VisUI;
import com.mbrlabs.mundus.entities.EntityShader;
import com.mbrlabs.mundus.terrain.TerrainShader;
import com.mbrlabs.mundus.ui.screens.MainScreen;

public class Mundus extends Game {

    public TerrainShader terrainShader;
    public EntityShader entityShader;

    public PerspectiveCamera cam;

	@Override
	public void create () {
        VisUI.load();
        ShaderProgram.pedantic = false;

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 0, 0);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        terrainShader = new TerrainShader();
        terrainShader.init();

        entityShader = new EntityShader();
        entityShader.init();

        setScreen(new MainScreen(this));
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
        VisUI.dispose();
    }


}
