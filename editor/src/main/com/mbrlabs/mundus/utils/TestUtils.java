/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.terrain.Terrain;

import java.util.Random;

/**
 * @author Marcus Brummer
 * @version 07-12-2015
 */
public class TestUtils {

    public static Array<ModelInstance> createABunchOfModelsOnTheTerrain(int count, Model model, Terrain terrain) {
        Array<ModelInstance> boxInstances = new Array<>();
        Random rand = new Random();

        Vector3 tv3 = new Vector3();

        for (int i = 0; i < count; i++) {
            ModelInstance mi = new ModelInstance(model);
            terrain.transform.getTranslation(tv3);
            mi.transform.setTranslation(tv3);
            float x = terrain.terrainWidth * rand.nextFloat();
            float z = terrain.terrainDepth * rand.nextFloat();
            float y = terrain.getHeightAtWorldCoord(x, z);
            mi.transform.translate(x, y, z);
            boxInstances.add(mi);
        }

        return boxInstances;
    }

}
