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

    private MTexture base;
    private MTexture chanR;
    private MTexture chanG;
    private MTexture chanB;
    private MTexture chanA;
    private MTexture splat;

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
        return base.getId() == -1;
    }

    public MTexture getBase() {
        return base;
    }

    public void setBase(MTexture base) {
        this.base = base;
    }

    public MTexture getChanR() {
        return chanR;
    }

    public void setChanR(MTexture chanR) {
        this.chanR = chanR;
    }

    public MTexture getChanG() {
        return chanG;
    }

    public void setChanG(MTexture chanG) {
        this.chanG = chanG;
    }

    public MTexture getChanB() {
        return chanB;
    }

    public void setChanB(MTexture chanB) {
        this.chanB = chanB;
    }

    public MTexture getChanA() {
        return chanA;
    }

    public void setChanA(MTexture chanA) {
        this.chanA = chanA;
    }

    public MTexture getSplat() {
        return splat;
    }

    public void setSplat(MTexture splat) {
        this.splat = splat;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

}
