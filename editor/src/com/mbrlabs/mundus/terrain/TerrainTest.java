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
        terrain = new Terrain(64, 64, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal  );
        Pixmap heightMap = new Pixmap(Gdx.files.internal("data/heightmap.png"));
        terrain.loadHeightMap(heightMap);
        heightMap.dispose();
    }

}
