package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class TerrainBatch implements Disposable {

    private TerrainShader shader;
    private Array<Terrain> terrains;

    public TerrainBatch() {
        shader = new TerrainShader();
        shader.init();
        terrains = new Array<Terrain>();
    }

    public void render(RenderableProvider renderableProvider, Shader shader) {

    }

    @Override
    public void dispose() {
        shader.dispose();
    }

}
