package com.mbrlabs.mundus.terrain.brushes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
public class SphereBrush implements Brush, Disposable {

    private Model sphereModel;
    private ModelInstance sphereModelInstance;

    private float radius;

    public SphereBrush() {
        ModelBuilder modelBuilder = new ModelBuilder();
        sphereModel = modelBuilder.createSphere(1,1,1,30,30, new Material(), VertexAttributes.Usage.Position);
        sphereModelInstance = new ModelInstance(sphereModel);
    }

    @Override
    public void dispose() {
        sphereModel.dispose();
    }

    public ModelInstance getRenderable() {
        return this.sphereModelInstance;
    }



}
