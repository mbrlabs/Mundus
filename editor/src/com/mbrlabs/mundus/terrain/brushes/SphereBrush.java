package com.mbrlabs.mundus.terrain.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.Mundus;
import com.mbrlabs.mundus.input.UpdatableInputProcessor;
import com.mbrlabs.mundus.terrain.Terrain;

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
public class SphereBrush implements TerrainHeightBrush {

    public enum Mode {
        SHARP, SMOOTH
    }

    private static final float SIZE = 1;

    private Model sphereModel;
    private ModelInstance sphereModelInstance;
    private BoundingBox boundingBox = new BoundingBox();
    private float radius;

    private Mode mode = Mode.SMOOTH;

    private UpdatableInputProcessor inputProcessor;

    private Vector3 tVec0 = new Vector3();
    private Vector3 tVec1 = new Vector3();

    public SphereBrush() {
        ModelBuilder modelBuilder = new ModelBuilder();
        sphereModel = modelBuilder.createSphere(SIZE,SIZE,SIZE,30,30, new Material(), VertexAttributes.Usage.Position);
        sphereModelInstance = new ModelInstance(sphereModel);
        sphereModelInstance.calculateBoundingBox(boundingBox);
        scale(15);

        inputProcessor = new SphereBrushInputController();
    }

    public void scale(float amount) {
        sphereModelInstance.transform.scl(amount);
        radius = (boundingBox.getWidth()*sphereModelInstance.transform.getScaleX()) / 2f;
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

    @Override
    public UpdatableInputProcessor getInputProcessor() {
        return this.inputProcessor;
    }

    public ModelInstance getRenderable() {
        return this.sphereModelInstance;
    }

    /**
     *
     */
    private class SphereBrushInputController implements UpdatableInputProcessor {

        private static final int KEY_LOWER_TERRAIN = Input.Buttons.RIGHT;
        private static final int KEY_RAISE_TERRAIN = Input.Buttons.LEFT;

        private Vector3 tempV3 = new Vector3();

        @Override
        public boolean scrolled(int amount) {
            if(amount < 0) {
                scale(0.9f);
            } else {
                scale(1.1f);
            }
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            Terrain terrain = Mundus.projectContext.terrains.first();
            Ray ray = Mundus.cam.getPickRay(screenX, screenY);
            terrain.getRayIntersection(tempV3, ray);
            sphereModelInstance.transform.setTranslation(tempV3);
            return false;
        }

        @Override
        public void update() {
            if(Gdx.input.isButtonPressed(KEY_RAISE_TERRAIN)) {
                draw(Mundus.projectContext.terrains, true);
            }

            if(Gdx.input.isButtonPressed(KEY_LOWER_TERRAIN)) {
                draw(Mundus.projectContext.terrains, false);
            }
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return mouseMoved(screenX, screenY);
        }

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

    }

}
