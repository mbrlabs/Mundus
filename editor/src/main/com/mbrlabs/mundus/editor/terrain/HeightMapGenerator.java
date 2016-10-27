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

package com.mbrlabs.mundus.editor.terrain;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.terrain.Terrain;

import java.nio.ByteBuffer;

/**
 *
 * @author Marcus Brummer
 * @version 20-06-2016
 */
public class HeightMapGenerator extends Generator<HeightMapGenerator> {

    private Pixmap map;

    HeightMapGenerator(Terrain terrain) {
        super(terrain);
    }

    public HeightMapGenerator map(Pixmap map) {
        this.map = map;
        return this;
    }

    @Override
    public void terraform() {
        if (map.getWidth() != terrain.vertexResolution || map.getHeight() != terrain.vertexResolution) {
            throw new GdxRuntimeException("Incorrect map size");
        }
        terrain.heightData = heightColorsToMap(map.getPixels(), map.getFormat(), terrain.vertexResolution,
                terrain.vertexResolution, maxHeight);
        terrain.update();
    }

    // Simply creates an array containing only all the red components of the
    // heightData.
    private float[] heightColorsToMap(final ByteBuffer data, final Pixmap.Format format, int width, int height,
            float maxHeight) {
        final int bytesPerColor = (format == Pixmap.Format.RGB888 ? 3 : (format == Pixmap.Format.RGBA8888 ? 4 : 0));
        if (bytesPerColor == 0) throw new GdxRuntimeException("Unsupported format, should be either RGB8 or RGBA8");
        if (data.remaining() < (width * height * bytesPerColor)) throw new GdxRuntimeException("Incorrect map size");

        final int startPos = data.position();
        byte[] source = null;
        int sourceOffset = 0;
        if (data.hasArray() && !data.isReadOnly()) {
            source = data.array();
            sourceOffset = data.arrayOffset() + startPos;
        } else {
            source = new byte[width * height * bytesPerColor];
            data.get(source);
            data.position(startPos);
        }

        float[] dest = new float[width * height];
        for (int i = 0; i < dest.length; ++i) {
            int v = source[sourceOffset + i * bytesPerColor];
            v = v < 0 ? 256 + v : v;
            dest[i] = maxHeight * ((float) v / 255f);
        }

        return dest;
    }

}
