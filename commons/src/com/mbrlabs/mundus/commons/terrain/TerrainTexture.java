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

package com.mbrlabs.mundus.commons.terrain;

import com.mbrlabs.mundus.commons.model.MTexture;

/**
 * @author Marcus Brummer
 * @version 28-01-2016
 */
public class TerrainTexture {

    private SplatTexture base;
    private SplatTexture chanR;
    private SplatTexture chanG;
    private SplatTexture chanB;
    private SplatTexture chanA;
    private SplatMap splatmap;

    private Terrain terrain;

    public int countSplatChannelTextures() {
        int count = 0;
        if(chanR != null) count++;
        if(chanG != null) count++;
        if(chanB != null) count++;
        if(chanA != null) count++;

        return count;
    }

    public boolean hasDefaultBaseTexture() {
        return base.texture.getId() == -1;
    }

    public SplatTexture getBase() {
        return base;
    }

    public void setBase(SplatTexture base) {
        this.base = base;
    }

    public SplatTexture getChanR() {
        return chanR;
    }

    public void setChanR(SplatTexture chanR) {
        this.chanR = chanR;
    }

    public SplatTexture getChanG() {
        return chanG;
    }

    public void setChanG(SplatTexture chanG) {
        this.chanG = chanG;
    }

    public SplatTexture getChanB() {
        return chanB;
    }

    public void setChanB(SplatTexture chanB) {
        this.chanB = chanB;
    }

    public SplatTexture getChanA() {
        return chanA;
    }

    public void setChanA(SplatTexture chanA) {
        this.chanA = chanA;
    }

    public SplatMap getSplatmap() {
        return splatmap;
    }

    public void setSplatmap(SplatMap splatmap) {
        this.splatmap = splatmap;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

}
