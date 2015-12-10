package com.mbrlabs.mundus.terrain.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.terrain.Terrain;

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
public class SphereBrush implements Brush {

    public static final String NAME = "Sphere Brush";

    public enum Mode {
        SHARP, SMOOTH
    }

    private static final float SIZE = 1;

    private Model sphereModel;
    private ModelInstance sphereModelInstance;
    private BoundingBox boundingBox = new BoundingBox();
    private float radius;
    private Drawable icon;

    private Mode mode = Mode.SMOOTH;

    private Vector3 tVec0 = new Vector3();
    private Vector3 tVec1 = new Vector3();

    private Shader shader;

    public SphereBrush(Shader shader) {
        this.shader = shader;
        ModelBuilder modelBuilder = new ModelBuilder();
        sphereModel = modelBuilder.createSphere(SIZE,SIZE,SIZE,30,30, new Material(), VertexAttributes.Usage.Position);
        sphereModelInstance = new ModelInstance(sphereModel);
        sphereModelInstance.calculateBoundingBox(boundingBox);
        scale(15);

        icon = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/brushes/sphere.png"))));
    }

    public void scale(float amount) {
        sphereModelInstance.transform.scl(amount);
        radius = (boundingBox.getWidth()*sphereModelInstance.transform.getScaleX()) / 2f;
    }

    @Override
    public Drawable getIcon() {
        return this.icon;
    }

    @Override
    public String getName() {
        return SphereBrush.NAME;
    }

    public void draw(Array<Terrain> terrains, boolean up) {
        // TODO extend to work for all terrains
        Terrain terrain = terrains.first();
        for (int x = 0; x < terrain.vertexResolution; x++) {
            for (int z = 0; z <  terrain.vertexResolution; z++) {
                terrain.getVertexPosition(tVec0, x, z);
                float distance = tVec0.dst(this.sphereModelInstance.transform.getTranslation(tVec1));

                if(distance <= radius) {
                    float dir = up ? 1 : -1;
                    float elevation = 0;
                    if(mode == Mode.SMOOTH) {
                        elevation = (radius - distance) * 0.1f * dir;
                    } else {
                        elevation = dir;
                    }
                    terrain.heightData[z * terrain.vertexResolution + x] += elevation;
                }
            }
        }
        terrain.update();
    }

    @Override
    public void dispose() {
        sphereModel.dispose();
    }

    public void render(PerspectiveCamera cam, ModelBatch batch) {
        batch.begin(cam);
        batch.render(sphereModelInstance, shader);
        batch.end();
    }

    @Override
    public void setTranslation(Vector3 translation) {
        sphereModelInstance.transform.setTranslation(translation);
    }


}
