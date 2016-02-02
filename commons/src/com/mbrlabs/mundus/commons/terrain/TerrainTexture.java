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


import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 28-01-2016
 */
public class TerrainTexture {

    private Map<SplatTexture.Channel, SplatTexture> textures;
    private SplatMap splatmap;
    private Terrain terrain;
    
    public TerrainTexture() {
        textures = new HashMap<>(5, 1);
    }

    public SplatTexture getTexture(SplatTexture.Channel channel) {
        return textures.get(channel);
    }

    public void removeTexture(SplatTexture.Channel channel) {
        textures.remove(channel);
    }

    public void setSplatTexture(SplatTexture tex) {
        textures.put(tex.channel, tex);
    }

    public SplatTexture.Channel getNextFreeChannel() {
        // base
        SplatTexture st = textures.get(SplatTexture.Channel.BASE);
        if(st == null || st.texture.getId() == -1) return SplatTexture.Channel.BASE;
        // r
        st = textures.get(SplatTexture.Channel.R);
        if(st == null) return SplatTexture.Channel.R;
        // g
        st = textures.get(SplatTexture.Channel.G);
        if(st == null) return SplatTexture.Channel.G;
        // b
        st = textures.get(SplatTexture.Channel.B);
        if(st == null) return SplatTexture.Channel.B;
        // a
        st = textures.get(SplatTexture.Channel.A);
        if(st == null) return SplatTexture.Channel.A;

        return null;
    }

    public boolean hasTextureChannel(SplatTexture.Channel channel) {
        return textures.containsKey(channel);
    }

    public int countTextures() {
        return textures.size();
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
