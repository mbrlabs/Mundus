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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.utils.MathUtils;

/**
 * @author Marcus Brummer
 * @version 31-01-2016
 */
public class SplatMap {

    public static final int DEFAULT_SIZE = 512;

    private int width;
    private int height;

    private Pixmap pixmap;
    private Texture texture;
    private String path;

    private Color c0 = new Color();
    private Color c1 = new Color();

    public SplatMap(int width, int height) {
        Pixmap.setBlending(Pixmap.Blending.None);
        this.pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);

        texture = new Texture(pixmap);
        updateTexture();

        this.width = width;
        this.height = height;
    }

    public Texture getTexture() {
        return texture;
    }

    public void drawPixel(int x, int y, float strength, SplatTexture.Channel channel) {
        setColor(channel, strength);
        pixmap.drawPixel(x, y);
    }

    public void drawCircle(int x, int y, int radius, float strength, SplatTexture.Channel channel) {
        for(int smX = 0; smX < pixmap.getWidth(); smX++) {
            for(int smY = 0; smY < pixmap.getHeight(); smY++) {
                float dst = MathUtils.dst(x, y, smX, smY);
                if(dst <= radius) {
                    float edgeOpcity = (radius - dst) * 0.1f;
                    edgeOpcity *= strength;
                    int newPixelColor = addChannel(pixmap.getPixel(smX, smY), channel, edgeOpcity);
                    pixmap.drawPixel(smX, smY, newPixelColor);
                }
            }
        }
    }

    public void updateTexture() {
        texture.draw(pixmap, 0, 0);
    }

    public void savePNG(FileHandle fileHandle) {
        PixmapIO.writePNG(fileHandle, pixmap);
    }

    public void loadPNG(FileHandle fileHandle) {
        pixmap = new Pixmap(fileHandle);
        updateTexture();

        this.width = pixmap.getWidth();
        this.height = pixmap.getHeight();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private void setColor(SplatTexture.Channel channel, float strength) {
        if(channel == SplatTexture.Channel.R) {
            pixmap.setColor(strength, 0, 0, 0);
        } else if(channel == SplatTexture.Channel.G) {
            pixmap.setColor(0, strength, 0, 0);
        } else if(channel == SplatTexture.Channel.B) {
            pixmap.setColor(0, 0, strength, 0);
        } else if(channel == SplatTexture.Channel.A) {
            pixmap.setColor(0, 0, 0, strength);
        }
    }

    private int addChannel(int pixelColor, SplatTexture.Channel channel, float strength) {
        c0.set(pixelColor);
        if(channel == SplatTexture.Channel.R) {
            c0.add(strength, 0, 0, 0);
        } else if(channel == SplatTexture.Channel.G) {
            c0.add(0, strength, 0, 0);
        } else if(channel == SplatTexture.Channel.B) {
            c0.add(0, 0, strength, 0);
        } else if(channel == SplatTexture.Channel.A) {
            c0.add(0, 0, 0, strength);
        }

        return Color.rgba8888(c0);
    }

}
