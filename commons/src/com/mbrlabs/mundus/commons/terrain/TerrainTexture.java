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

    @Override
    public String toString() {
        return "TerrainTexture{" +
                "base=" + base +
                ", chanR=" + chanR +
                ", chanG=" + chanG +
                ", chanB=" + chanB +
                ", chanA=" + chanA +
                ", splat=" + splat +
                ", terrain=" + terrain +
                '}';
    }
}
