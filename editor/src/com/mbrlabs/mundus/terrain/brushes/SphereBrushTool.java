/*
 * Copyright (c) 2015. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.terrain.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.terrain.TerrainInstance;
import com.mbrlabs.mundus.tools.Tool;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
public class SphereBrushTool extends Tool {

    private static final int KEY_LOWER_TERRAIN = Input.Buttons.RIGHT;
    private static final int KEY_RAISE_TERRAIN = Input.Buttons.LEFT;

    public enum Mode {
        SHARP, SMOOTH
    }

    public static final String NAME = "Sphere Brush";
    private static final float SIZE = 1;

    private Model sphereModel;
    private ModelInstance sphereModelInstance;
    private BoundingBox boundingBox = new BoundingBox();
    private float radius;
    private Drawable icon;

    private Mode mode = Mode.SMOOTH;

    private Vector3 tVec0 = new Vector3();
    private Vector3 tVec1 = new Vector3();
    private Vector3 tempV3 = new Vector3();

    public SphereBrushTool(ProjectContext projectContext, Shader shader, ModelBatch modelBatch) {
        super(projectContext, shader, modelBatch);
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
    public String getName() {
        return NAME;
    }

    @Override
    public Drawable getIcon() {
        return this.icon;
    }

    @Override
    public void render() {
        batch.begin(projectContext.currScene.cam);
        batch.render(sphereModelInstance, shader);
        batch.end();
    }

    @Override
    public void act() {
        boolean up = true;
        if(Gdx.input.isButtonPressed(KEY_RAISE_TERRAIN)) {
            up = true;
        } else if(Gdx.input.isButtonPressed(KEY_LOWER_TERRAIN)) {
            up = false;
        } else {
            return;
        }

        // tVec1 holds sphere transformation
        sphereModelInstance.transform.getTranslation(tVec1);

        TerrainInstance terrainInstance = projectContext.currScene.terrainGroup.getTerrain(tVec1.x, tVec1.z);
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
        if(projectContext.currScene.terrainGroup.size() > 0) {
            Ray ray = projectContext.currScene.cam.getPickRay(screenX, screenY);
            projectContext.currScene.terrainGroup.getRayIntersection(tempV3, ray);
            sphereModelInstance.transform.setTranslation(tempV3);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }


}
