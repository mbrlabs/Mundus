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

package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.FloatArray;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainTexture;
import com.mbrlabs.mundus.utils.Log;
import com.mbrlabs.mundus.commons.utils.TextureUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Exports the terrain height.
 * Everything else (e.g. normals) can be calculated at runtime.
 *
 * @author Marcus Brummer
 * @version 04-12-2015
 */
public class TerrainIO {

    public static final String FILE_EXTENSION = "terra";

    /**
     * Binary gziped format.
     *
     * @param terrain
     * @param path
     */
    public static void exportTerrain(Terrain terrain, String path) {
        float[] data = terrain.heightData;
        long start = System.currentTimeMillis();

        // create file
        File file = new File(path);
        try {
            FileUtils.touch(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write data
        try(DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(
                new GZIPOutputStream(new FileOutputStream(file))))) {

            for(float f : data) {
                outputStream.writeFloat(f);
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.debug("Terrain export execution time (" + data.length + " floats): "
                + (System.currentTimeMillis() - start) + " ms");
    }

    public static Terrain importTerrain(Terrain terrain, String path) {
        FloatArray floatArray = new FloatArray();

        try(DataInputStream is = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(path))))) {
            while (is.available() > 0) {
                floatArray.add(is.readFloat());
            }
        } catch (EOFException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.debug("Terrain import. floats: " + floatArray.size);

        terrain.init();
        terrain.heightData = floatArray.toArray();
        terrain.update();

        TerrainTexture splat = terrain.getTerrainTexture();

        MTexture base = new MTexture();
        base.setId(-1);
        base.texture = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/chess.png"));

        splat.base = base;
//        terrainTexture.chanR = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/red_soil.jpg"));
//        terrainTexture.chanG = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/pebble.jpg"));
//        terrainTexture.chanB = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/grass.jpg"));
//        terrainTexture.chanA = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/stone_path.jpg"));
//        terrainTexture.terrainTexture = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrain/splat_map.png"));
        return terrain;
    }

}
