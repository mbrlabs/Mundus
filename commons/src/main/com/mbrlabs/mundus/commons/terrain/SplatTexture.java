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

import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.utils.TextureProvider;

/**
 * @author Marcus Brummer
 * @version 01-02-2016
 */
public class SplatTexture implements TextureProvider {

    public enum Channel {
        BASE, R, G, B, A
    }

    public Channel channel;
    public TextureAsset texture;

    public SplatTexture(Channel channel, TextureAsset texture) {
        this.channel = channel;
        this.texture = texture;
    }

    @Override
    public Texture getTexture() {
        if (texture != null) {
            return texture.getTexture();
        }
        return null;
    }

}
