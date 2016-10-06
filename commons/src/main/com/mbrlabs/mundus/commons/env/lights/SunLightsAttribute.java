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

package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.Array;

/**
 * @author Marcus Brummer
 * @version 04-01-2016
 */
public class SunLightsAttribute extends Attribute {

    public final static String Alias = "sunLights";
    public final Array<SunLight> lights;

    public final static long Type = register(Alias);

    public final static boolean is(final long mask) {
        return (mask & Type) == mask;
    }

    public SunLightsAttribute() {
        super(Type);
        lights = new Array<SunLight>(1);
    }

    public SunLightsAttribute(final SunLightsAttribute copyFrom) {
        this();
        lights.addAll(copyFrom.lights);
    }

    @Override
    public SunLightsAttribute copy() {
        return new SunLightsAttribute(this);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        for (SunLight light : lights)
            result = 1237 * result + (light == null ? 0 : light.hashCode());
        return result;
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        return 0; // FIXME implement comparing
    }

}
