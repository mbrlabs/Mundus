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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.assets.PixmapTextureAsset;

/**
 * @author Marcus Brummer
 * @version 31-01-2016
 */
public class SplatMap {

    public static final int DEFAULT_SIZE = 512;

    private int width;
    private int height;

    private PixmapTextureAsset pixmapAsset;

    private final Color c0 = new Color();

    public SplatMap(PixmapTextureAsset asset) {
        this.pixmapAsset = asset;
        this.pixmapAsset.getPixmap().setBlending(Pixmap.Blending.None);

        this.width = asset.getPixmap().getWidth();
        this.height = asset.getPixmap().getHeight();
    }

    public Texture getTexture() {
        return pixmapAsset.getTexture();
    }

    public Pixmap getPixmap() {
        return pixmapAsset.getPixmap();
    }

    public void clearChannel(SplatTexture.Channel channel) {
        Pixmap pixmap = getPixmap();
        for (int smX = 0; smX < pixmap.getWidth(); smX++) {
            for (int smY = 0; smY < pixmap.getHeight(); smY++) {
                c0.set(pixmap.getPixel(smX, smY));
                if (channel == SplatTexture.Channel.R) {
                    c0.set(0, c0.g, c0.b, c0.a);
                } else if (channel == SplatTexture.Channel.G) {
                    c0.set(c0.r, 0, c0.b, c0.a);
                } else if (channel == SplatTexture.Channel.B) {
                    c0.set(c0.r, c0.g, 0, c0.a);
                } else if (channel == SplatTexture.Channel.A) {
                    c0.set(c0.r, c0.g, c0.b, 0);
                }
                pixmap.drawPixel(smX, smY, Color.rgba8888(c0));
            }
        }
    }

    public void clear() {
        Pixmap pixmap = getPixmap();
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fillRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
        updateTexture();
    }

    public void updateTexture() {
        getTexture().draw(getPixmap(), 0, 0);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int additiveBlend(int pixelColor, SplatTexture.Channel channel, float strength) {
        c0.set(pixelColor);
        if (channel == SplatTexture.Channel.BASE) {
            c0.sub(strength, strength, strength, strength);
        } else if (channel == SplatTexture.Channel.R) {
            c0.add(strength, 0, 0, 0);
        } else if (channel == SplatTexture.Channel.G) {
            c0.add(0, strength, 0, 0);
        } else if (channel == SplatTexture.Channel.B) {
            c0.add(0, 0, strength, 0);
        } else if (channel == SplatTexture.Channel.A) {
            c0.add(0, 0, 0, strength);
        }

        // prevent the sum to be greater than 1
        final float sum = c0.r + c0.g + c0.b + c0.a;
        if (sum > 1f) {
            final float correction = 1f / sum;
            c0.r *= correction;
            c0.g *= correction;
            c0.b *= correction;
            c0.a *= correction;
        }

        return Color.rgba8888(c0);
    }

}
