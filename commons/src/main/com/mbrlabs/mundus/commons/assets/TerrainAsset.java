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
package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.FloatArray;
import com.mbrlabs.mundus.commons.terrain.Terrain;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @author Marcus Brummer
 * @version 01-10-2016
 */
public class TerrainAsset extends Asset {

    private float[] data;

    // dependencies
    private PixmapTextureAsset splatmap;
    private TextureAsset detailBase;
    private TextureAsset detailR;
    private TextureAsset detailG;
    private TextureAsset detailB;
    private TextureAsset detailA;

    private Terrain terrain;

    public TerrainAsset(MetaFile meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    public float[] getData() {
        return data;
    }

    public PixmapTextureAsset getSplatmap() {
        return splatmap;
    }

    public void setSplatmap(PixmapTextureAsset splatmap) {
        this.splatmap = splatmap;
    }

    public TextureAsset getDetailBase() {
        return detailBase;
    }

    public void setDetailBase(TextureAsset detailBase) {
        this.detailBase = detailBase;
    }

    public TextureAsset getDetailR() {
        return detailR;
    }

    public void setDetailR(TextureAsset detailR) {
        this.detailR = detailR;
    }

    public TextureAsset getDetailG() {
        return detailG;
    }

    public void setDetailG(TextureAsset detailG) {
        this.detailG = detailG;
    }

    public TextureAsset getDetailB() {
        return detailB;
    }

    public void setDetailB(TextureAsset detailB) {
        this.detailB = detailB;
    }

    public TextureAsset getDetailA() {
        return detailA;
    }

    public void setDetailA(TextureAsset detailA) {
        this.detailA = detailA;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    @Override
    public void load() {
        // TODO create terrain here
        final FloatArray floatArray = new FloatArray();

        DataInputStream is;
        try {
            is = new DataInputStream(new BufferedInputStream(new GZIPInputStream(file.read())));
            while (is.available() > 0) {
                floatArray.add(is.readFloat());
            }
            is.close();
        } catch (EOFException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        data = floatArray.toArray();
    }

    @Override
    public void applyDependencies() {
        // TODO apply texture assets to terrain
    }

    @Override
    public void dispose() {

    }
}
