/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.tools.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 09-01-2016
 */
//public class FlattenBrush extends SphereBrush {
//
//    private static final String NAME = "Flatten Brush";
//    private static final int KEY_LOWER_TERRAIN = Input.Buttons.LEFT;
//
//
//    public FlattenBrush(ProjectContext projectContext, Shader shader, ModelBatch modelBatch) {
//        super(projectContext, shader, modelBatch);
//    }
//
//    @Override
//    public boolean supportsMode(BrushMode mode) {
//        switch (mode) {
//            case PAINT_HEIGHT: return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public String getIconFont() {
//        return Fa.MINUS;
//    }
//
//    @Override
//    public String getName() {
//        return NAME;
//    }
//
//    @Override
//    public void act() {
//        if(!Gdx.input.isButtonPressed(KEY_LOWER_TERRAIN)) {
//            return;
//        }
//
//        // tVec1 holds sphere transformation
//        sphereModelInstance.transform.getTranslation(tVec1);
//
//        Terrain terrain = projectContext.currScene.terrainGroup.getTerrain(tVec1.x, tVec1.z);
//        if(terrain == null) {
//            return;
//        }
//
//        final Vector3 terPos = terrain.getPosition();
//
//        for (int x = 0; x < terrain.vertexResolution; x++) {
//            for (int z = 0; z <  terrain.vertexResolution; z++) {
//                terrain.getVertexPosition(tVec0, x, z);
//                tVec0.x += terPos.x;
//                tVec0.z += terPos.z;
//                float distance = tVec0.dst(tVec1);
//
//                if(distance <= radius) {
//                    int heightIndex = z * terrain.vertexResolution + x;
//                    terrain.heightData[heightIndex] *= distance / radius;
//                }
//            }
//        }
//        terrain.update();
//    }
//
//
//}
