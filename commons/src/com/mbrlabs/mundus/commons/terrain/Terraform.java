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

package com.mbrlabs.mundus.commons.terrain;


import java.util.Random;

/**
 * @author Marcus Brummer
 * @version 20-06-2016
 */
public class Terraform {

    /**
     * Applies perlin noise to the terrain.
     *
     * @param terrain   terrain to which the perlin noise should be applied.
     */
    public static void perlin(Terrain terrain, float amplitude, int seed) {
        for(int i = 0; i < terrain.heightData.length; i++) {
            terrain.heightData[i] = new Random().nextFloat() * 50;
        }

        terrain.update();
    }

}
