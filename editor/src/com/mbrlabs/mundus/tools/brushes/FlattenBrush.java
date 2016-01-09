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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.terrain.TerrainInstance;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 09-01-2016
 */
public class FlattenBrush extends SphereBrush {

    private static final String NAME = "Flatten Brush";
    private static final int KEY_LOWER_TERRAIN = Input.Buttons.LEFT;


    public FlattenBrush(ProjectContext projectContext, Shader shader, ModelBatch modelBatch) {
        super(projectContext, shader, modelBatch);
    }

    @Override
    public String getIconFont() {
        return Fa.MINUS;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void act() {
        if(!Gdx.input.isButtonPressed(KEY_LOWER_TERRAIN)) {
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
                    int heightIndex = z * terrainInstance.terrain.vertexResolution + x;
                    terrainInstance.terrain.heightData[heightIndex] *= distance / radius;
                }
            }
        }
        terrainInstance.terrain.update();
    }


}
