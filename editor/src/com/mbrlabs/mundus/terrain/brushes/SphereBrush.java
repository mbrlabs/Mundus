package com.mbrlabs.mundus.terrain.brushes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.terrain.Terrain;

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
public class SphereBrush implements Brush, Disposable {

    private static final float SIZE = 1;

    private Model sphereModel;
    private ModelInstance sphereModelInstance;
    private BoundingBox boundingBox = new BoundingBox();
    private float radius;

    private Vector3 tVec0 = new Vector3();
    private Vector3 tVec1 = new Vector3();

    public SphereBrush() {
        ModelBuilder modelBuilder = new ModelBuilder();
        sphereModel = modelBuilder.createSphere(SIZE,SIZE,SIZE,30,30, new Material(), VertexAttributes.Usage.Position);
        sphereModelInstance = new ModelInstance(sphereModel);
        sphereModelInstance.calculateBoundingBox(boundingBox);
        radius = (boundingBox.getWidth()*sphereModelInstance.transform.getScaleX()) / 2f;
    }

    public void scale(float amount) {
        sphereModelInstance.transform.scl(amount);
        radius = (boundingBox.getWidth()*sphereModelInstance.transform.getScaleX()) / 2f;
    }

    public float getRadius() {
        return radius;
    }

    public void apply(Terrain terrain) {
        for (int x = 0; x < terrain.vertexResolution; x++) {
            for (int z = 0; z <  terrain.vertexResolution; z++) {
                terrain.calculatePositionAt(tVec0, x, z);
                float distance = tVec0.dst(this.sphereModelInstance.transform.getTranslation(tVec1));
                if(distance <= radius*2) {
                    int heightIndex = z * terrain.vertexResolution + x;
//                    if(terrain.heightData[heightIndex] <= 0) {
//                        terrain.heightData[heightIndex] = 0.01f;
//                    } else {
//                        terrain.heightData[z * terrain.vertexResolution + x] *= 1.01f;
//                    }
                    terrain.heightData[z * terrain.vertexResolution + x] += 0.4f;
                }
            }
        }
        terrain.update();
    }

    @Override
    public void dispose() {
        sphereModel.dispose();
    }

    public ModelInstance getRenderable() {
        return this.sphereModelInstance;
    }



}
