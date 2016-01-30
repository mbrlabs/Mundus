package com.mbrlabs.mundus.commons.terrain;

import com.mbrlabs.mundus.commons.model.MTexture;

/**
 * @author Marcus Brummer
 * @version 28-01-2016
 */
public class TerrainTextureSplat {

    public MTexture base;
    public MTexture chanR;
    public MTexture chanG;
    public MTexture chanB;
    public MTexture chanA;
    public MTexture splat;

    public Terrain terrain;

    public int countSplatDetailTextures() {
        int count = 0;
        if(chanR != null) count++;
        if(chanG != null) count++;
        if(chanB != null) count++;
        if(chanA != null) count++;

        return count;
    }

    public boolean hasDefaultTexture() {
        return base.getId() == -1;
    }



}
