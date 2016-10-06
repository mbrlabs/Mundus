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

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

/**
 * @author Marcus Brummer
 * @version 28-01-2016
 */
public class TerrainTextureAttribute extends Attribute {

    public static final String ATTRIBUTE_SPLAT0_ALIAS = "splat0";
    public static final long ATTRIBUTE_SPLAT0 = register(ATTRIBUTE_SPLAT0_ALIAS);
    public static final String ATTRIBUTE_SPLAT1_ALIAS = "splat1";
    public static final long ATTRIBUTE_SPLAT1 = register(ATTRIBUTE_SPLAT1_ALIAS);

    public TerrainTexture terrainTexture;

    protected static long Mask = ATTRIBUTE_SPLAT0 | ATTRIBUTE_SPLAT1;

    /**
     * Method to check whether the specified type is a valid DoubleAttribute
     * type
     */
    public static Boolean is(final long type) {
        return (type & Mask) != 0;
    }

    public TerrainTextureAttribute(long type, TerrainTexture terrainTexture) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        this.terrainTexture = terrainTexture;
    }

    public TerrainTextureAttribute(TerrainTextureAttribute other) {
        super(other.type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        this.terrainTexture = other.terrainTexture;
    }

    protected TerrainTextureAttribute(long type) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
    }

    @Override
    public Attribute copy() {
        return new TerrainTextureAttribute(this);
    }

    @Override
    public int hashCode() {
        final int prime = 7;
        final long v = NumberUtils.doubleToLongBits(terrainTexture.hashCode());
        return prime * super.hashCode() + (int) (v ^ (v >>> 32));
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        TerrainTexture otherValue = ((TerrainTextureAttribute) o).terrainTexture;
        return terrainTexture.equals(otherValue) ? 0 : -1;
    }

}
