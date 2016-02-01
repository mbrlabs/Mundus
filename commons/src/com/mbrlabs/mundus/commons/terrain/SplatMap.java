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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;

/**
 * @author Marcus Brummer
 * @version 31-01-2016
 */
public class SplatMap {

    public static final int DEFAULT_SIZE = 512;

    private Pixmap pixmap;
    private Texture texture;
    private String path;

    public SplatMap(int width, int height) {
        Pixmap.setBlending(Pixmap.Blending.None);
        this.pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);

        texture = new Texture(pixmap);
        updateTexture();
    }

    public Texture getTexture() {
        return texture;
    }

    public void drawPixel(int x, int y, float strength, SplatTexture.Channel channel) {
        setColor(channel, strength);
        pixmap.drawPixel(x, y);
    }

    public void drawCircle(int x, int y, int radius, float strength, SplatTexture.Channel channel) {
        setColor(channel, strength);
        pixmap.fillCircle(x, y, radius);
    }

    public void updateTexture() {
        texture.draw(pixmap, 0, 0);
    }

    public void savePNG(FileHandle fileHandle) {
        PixmapIO.writePNG(fileHandle, pixmap);
    }

    public void loadPNG(FileHandle fileHandle) {
        pixmap = new Pixmap(fileHandle);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

}
