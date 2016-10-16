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

/**
 * Exports the terrainAsset height. Everything else (e.g. normals) can be calculated
 * at runtime.
 *
 * @author Marcus Brummer
 * @version 04-12-2015
 */
public class TerrainIO {

    public static final String FILE_EXTENSION = "terra";

    //    /**
    //     * Binary gziped format.
    //     *
    //     * @param terrainAsset
    //     */
    //    public static void exportTerrain(ProjectContext projectContext, Terrain terrainAsset) {
    //        float[] data = terrainAsset.heightData;
    //        long start = System.currentTimeMillis();
    //
    //        // create file
    //        File file = new File(FilenameUtils.concat(projectContext.path, terrainAsset.terraPath));
    //        try {
    //            FileUtils.touch(file);
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //
    //        // write .terra
    //        try (DataOutputStream outputStream = new DataOutputStream(
    //                new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file))))) {
    //
    //            for (float f : data) {
    //                outputStream.writeFloat(f);
    //            }
    //            outputStream.flush();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //            return;
    //        }
    //
    //        // write splatmap
    //        SplatMap splatmap = terrainAsset.getTerrainTexture().getSplatmap();
    //        if (splatmap != null) {
    //            splatmap.savePNG(Gdx.files.absolute(FilenameUtils.concat(projectContext.path, splatmap.getPath())));
    //        }
    //
    //        // Log.debug("Terrain export execution time (" + data.length + "
    //        // floats): "
    //        // + (System.currentTimeMillis() - start) + " ms");
    //    }
    //
    //    public static Terrain importTerrain(ProjectContext projectContext, Terrain terrainAsset) {
    //        FloatArray floatArray = new FloatArray();
    //
    //        String terraPath = FilenameUtils.concat(projectContext.path, terrainAsset.terraPath);
    //        try (DataInputStream is = new DataInputStream(
    //                new BufferedInputStream(new GZIPInputStream(new FileInputStream(terraPath))))) {
    //            while (is.available() > 0) {
    //                floatArray.add(is.readFloat());
    //            }
    //        } catch (EOFException e) {
    //            // e.printStackTrace();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        // Log.debug("Terrain import. floats: {}", floatArray.size);
    //
    //        terrainAsset.heightData = floatArray.toArray();
    //        terrainAsset.init();
    //        terrainAsset.update();
    //
    //        // set default terrainAsset base texture if none is present
    //        TerrainTexture terrainTexture = terrainAsset.getTerrainTexture();
    //        if (terrainTexture.getTexture(SplatTexture.Channel.BASE) == null) {
    //            MTexture base = new MTexture();
    //            base.setId(-1);
    //            base.texture = TextureUtils.loadMipmapTexture(Gdx.files.internal("textures/terrainAsset/chessboard.png"), true);
    //            terrainTexture.setSplatTexture(new SplatTexture(SplatTexture.Channel.BASE, base));
    //        }
    //
    //        // load splat map if available
    //        SplatMap splatmap = terrainTexture.getSplatmap();
    //        if (splatmap != null) {
    //            String splatPath = FilenameUtils.concat(projectContext.path, splatmap.getPath());
    //            splatmap.loadPNG(Gdx.files.absolute(splatPath));
    //        }
    //
    //        return terrainAsset;
    //    }

}
