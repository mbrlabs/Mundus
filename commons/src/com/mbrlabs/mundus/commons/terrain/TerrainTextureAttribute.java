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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class TerrainTextureAttribute extends TextureAttribute {

    public static final String ATTRIBUTE_CHANNEL_R_ALIAS  = "chan_r";
    public static final long ATTRIBUTE_CHANNEL_R  = register(ATTRIBUTE_CHANNEL_R_ALIAS);
    public static final String ATTRIBUTE_CHANNEL_G_ALIAS  = "chan_g";
    public static final long ATTRIBUTE_CHANNEL_G  = register(ATTRIBUTE_CHANNEL_G_ALIAS);
    public static final String ATTRIBUTE_CHANNEL_B_ALIAS  = "chan_b";
    public static final long ATTRIBUTE_CHANNEL_B  = register(ATTRIBUTE_CHANNEL_B_ALIAS);
    public static final String ATTRIBUTE_CHANNEL_A_ALIAS  = "chan_a";
    public static final long ATTRIBUTE_CHANNEL_A  = register(ATTRIBUTE_CHANNEL_A_ALIAS);
    public static final String ATTRIBUTE_BLEND_MAP_ALIAS  = "blend_map";
    public static final long ATTRIBUTE_BLEND_MAP  = register(ATTRIBUTE_BLEND_MAP_ALIAS);

    static {
        Mask |= ATTRIBUTE_CHANNEL_R |
                ATTRIBUTE_CHANNEL_G |
                ATTRIBUTE_CHANNEL_B |
                ATTRIBUTE_CHANNEL_A |
                ATTRIBUTE_BLEND_MAP;
    }

    private TerrainTextureAttribute() {
        super(0);
    }


    public TerrainTextureAttribute(long type, Texture texture) {
        super(type, texture);
    }

    public static TerrainTextureAttribute createRChannel(final Texture texture) {
        return new TerrainTextureAttribute(ATTRIBUTE_CHANNEL_R, texture);
    }

    public static TerrainTextureAttribute createGChannel(final Texture texture) {
        return new TerrainTextureAttribute(ATTRIBUTE_CHANNEL_G, texture);
    }

    public static TerrainTextureAttribute createBChannel(final Texture texture) {
        return new TerrainTextureAttribute(ATTRIBUTE_CHANNEL_B, texture);
    }

    public static TerrainTextureAttribute createAChannel(final Texture texture) {
        return new TerrainTextureAttribute(ATTRIBUTE_CHANNEL_A, texture);
    }

    public static TerrainTextureAttribute createBlendMap(final Texture texture) {
        return new TerrainTextureAttribute(ATTRIBUTE_BLEND_MAP, texture);
    }

}
