package com.mbrlabs.mundus.terrain.brushes;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.Tool;
import com.mbrlabs.mundus.terrain.Terrain;

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
public interface TerrainHeightBrush extends Disposable, Tool {

    public void draw(Array<Terrain> terrains, boolean up);
    public ModelInstance getRenderable();
    public void scale(float scale);

}
