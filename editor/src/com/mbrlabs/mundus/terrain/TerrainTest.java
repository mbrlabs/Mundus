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
        terrain = new Terrain(180, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        Pixmap heightMap = new Pixmap(Gdx.files.internal("heightmaps/hm3.jpg"));
        terrain.loadHeightMap(heightMap, 190);
        terrain.update();
        Texture tex = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/grass.png"));

        terrain.setTexture(tex);
        heightMap.dispose();

        //TerrainIO.exportBinary(terrain, "/home/marcus/Desktop/test.ter");

      // terrain = TerrainIO.importBinary("/home/marcus/Desktop/test.ter");
    }

}
