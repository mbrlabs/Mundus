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
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.terrain.TerrainGroup;
import com.mbrlabs.mundus.terrain.TerrainInstance;

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

    public void draw(TerrainGroup terrainGroup, boolean up) {
        // tVec1 holds sphere transformation
        sphereModelInstance.transform.getTranslation(tVec1);

        TerrainInstance terrainInstance = terrainGroup.getTerrain(tVec1.x, tVec1.z);
        if(terrainInstance == null) {
            return;
        }

        final Vector3 terPos = terrainInstance.getPosition();

        for (int x = 0; x < terrainInstance.terrain.vertexResolution; x++) {
            for (int z = 0; z <  terrainInstance.terrain.vertexResolution; z++) {
                terrainInstance.terrain.getVertexPosition(tVec0, x, z);
                tVec0.x += terPos.x;
                tVec0.z += terPos.z;
                float distance = tVec0.dst(tVec1);

                if(distance <= radius) {
                    float dir = up ? 1 : -1;
                    float elevation = 0;
                    if(mode == Mode.SMOOTH) {
                        elevation = (radius - distance) * 0.1f * dir;
                    } else {
                        elevation = dir;
                    }
                    terrainInstance.terrain.heightData[z * terrainInstance.terrain.vertexResolution + x] += elevation;
                }
            }
        }
        terrainInstance.terrain.update();
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
