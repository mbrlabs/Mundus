package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
public class TerrainTest {

    public Terrain terrain;

    public TerrainTest() {
        terrain = new Terrain(128, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Pixmap heightMap = new Pixmap(Gdx.files.internal("data/heightmap128.png"));
        terrain.loadHeightMap(heightMap, 190);
        terrain.update();
        heightMap.dispose();
    }

}
