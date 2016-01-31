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

package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.model.MTexture;

/**
 * @author Marcus Brummer
 * @version 31-01-2016
 */
public class Splatmap extends MTexture {

    public enum Channel {
        R, G, B, A
    }

    private Pixmap pixmap;

    public Splatmap(int width, int height) {
        Pixmap.setBlending(Pixmap.Blending.None);
        this.pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
        texture = new Texture(pixmap);
    }

    public void drawPixel(int x, int y, float strength, Channel channel) {
        setColor(channel, strength);
        pixmap.drawPixel(x, y);
    }

    public void drawCircle(int x, int y, int radius, float strength, Channel channel) {
        setColor(channel, strength);
        pixmap.fillCircle(x, y, radius);
    }

    public void updateTexture() {
        texture.draw(pixmap, 0, 0);
    }

    public void saveAsPNG(FileHandle fileHandle) {
        PixmapIO.writePNG(fileHandle, pixmap);
    }

    private void setColor(Channel channel, float strength) {
        if(channel == Channel.R) {
            pixmap.setColor(strength, 0, 0, 0);
        } else if(channel == Channel.G) {
            pixmap.setColor(0, strength, 0, 0);
        } else if(channel == Channel.B) {
            pixmap.setColor(0, 0, strength, 0);
        } else if(channel == Channel.A) {
            pixmap.setColor(0, 0, 0, strength);
        }
    }

}
