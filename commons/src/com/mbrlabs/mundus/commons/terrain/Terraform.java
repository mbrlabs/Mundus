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


import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Provides static methods to manipulate the height of terrains.
 *
 * @author Marcus Brummer
 * @version 20-06-2016
 */
public class Terraform {

    private static final Random rand = new Random();

    /**
     * Applies perlin noise to the terrain.
     *
     * @param terrain   terrain to which the perlin noise should be applied.
     */
    public static void perlin(Terrain terrain, float amplitude, int seed) {
        rand.setSeed(seed);

        for(int i = 0; i < terrain.heightData.length; i++) {
            int x = i % terrain.vertexResolution;
            int z = (int) Math.floor((double)i / terrain.vertexResolution);

            float height = getInterpolatedNoise(seed, x / 8f, z / 8f);
            height *= amplitude;

            terrain.heightData[z * terrain.vertexResolution + x] = height;
        }

        terrain.update();
    }

    private static float interpolate(float a, float b, float blend) {
        double theta = blend * Math.PI;
        float f = (float) (1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
    }

    private static float getNoise(long seed, int x, int z) {
        rand.setSeed(x * 49632 + z * 325176 + seed);
        return rand.nextFloat() * 2f - 1f;
    }

    private static float getInterpolatedNoise(long seed, float x, float z){
        int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;

        float v1 = getSmoothNoise(seed, intX, intZ);
        float v2 = getSmoothNoise(seed, intX + 1, intZ);
        float v3 = getSmoothNoise(seed, intX, intZ + 1);
        float v4 = getSmoothNoise(seed, intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fracX);
        float i2 = interpolate(v3, v4, fracX);
        return interpolate(i1, i2, fracZ);
    }

    private static float getSmoothNoise(long seed, int x, int z) {
        float corners = getNoise(seed, x+1, z-1) + getNoise(seed, x+1, z-1) +
                getNoise(seed, x-1, z+1) + getNoise(seed, x+1, z+1);
        corners /= 16f;
        float sides = getNoise(seed, x-1, z) + getNoise(seed, x+1, z) +
                getNoise(seed, x, z-1) + getNoise(seed, x, z+1);
        sides /= 8f;
        float center = getNoise(seed, x, z) / 4f;

        return corners + sides + center;
    }

    /**
     * Loads a heightmap.
     *
     * The pixmap dimensions must match the terrain size, otherwise a GdxRuntimeException is thrown.
     *
     * @param terrain   the terrain
     * @param map       height map
     * @param maxHeight max amplitude
     */
    public static void heightMap(Terrain terrain, Pixmap map, float maxHeight) {
        if (map.getWidth() != terrain.vertexResolution ||
                map.getHeight() != terrain.vertexResolution) {
            throw new GdxRuntimeException("Incorrect map size");
        }
        terrain.heightData = heightColorsToMap(map.getPixels(), map.getFormat(),
                terrain.vertexResolution, terrain.vertexResolution, maxHeight);
        terrain.update();
    }

    // Simply creates an array containing only all the red components of the heightData.
    private static float[] heightColorsToMap (final ByteBuffer data, final Pixmap.Format format, int width, int height, float maxHeight) {
        final int bytesPerColor = (format == Pixmap.Format.RGB888 ? 3 : (format == Pixmap.Format.RGBA8888 ? 4 : 0));
        if (bytesPerColor == 0) throw new GdxRuntimeException("Unsupported format, should be either RGB8 or RGBA8");
        if (data.remaining() < (width * height * bytesPerColor)) throw new GdxRuntimeException("Incorrect map size");

        final int startPos = data.position();
        byte[] source = null;
        int sourceOffset = 0;
        if (data.hasArray() && !data.isReadOnly()) {
            source = data.array();
            sourceOffset = data.arrayOffset() + startPos;
        } else {
            source = new byte[width * height * bytesPerColor];
            data.get(source);
            data.position(startPos);
        }

        float[] dest = new float[width * height];
        for (int i = 0; i < dest.length; ++i) {
            int v = source[sourceOffset + i * 3];
            v = v < 0 ? 256 + v : v;
            dest[i] = maxHeight * ((float)v / 255f);
        }

        return dest;
    }

}
