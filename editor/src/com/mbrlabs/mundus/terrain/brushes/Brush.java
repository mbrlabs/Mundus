package com.mbrlabs.mundus.terrain.brushes;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.terrain.TerrainGroup;

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
public interface Brush extends Disposable {

    public void draw(TerrainGroup terrainGroup, boolean up);
    public void scale(float scale);

    public Drawable getIcon();
    public String getName();

    public void render(PerspectiveCamera cam, ModelBatch modelBatch);

    public void setTranslation(Vector3 translation);

}
