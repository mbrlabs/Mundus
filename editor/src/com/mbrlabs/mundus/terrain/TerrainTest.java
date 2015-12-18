package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.mbrlabs.mundus.utils.TextureUtils;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
public class TerrainTest {

    public Terrain terrain;

    public TerrainTest() {
        terrain = new Terrain(180);
        Pixmap heightMap = new Pixmap(Gdx.files.internal("heightmaps/heightmap180.png"));
        terrain.loadHeightMap(heightMap, 190);
        terrain.update();
        Texture tex = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/stone_hr.jpg"));
        terrain.setTexture(tex);
        heightMap.dispose();
    }

}
