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

package com.mbrlabs.mundus.editor.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

/**
 * @author Marcus Brummer
 * @version 06-01-2016
 */
public class FogDescriptor {

    @Tag(0)
    private float density;
    @Tag(1)
    private float gradient;
    @Tag(2)
    private int color;

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getGradient() {
        return gradient;
    }

    public void setGradient(float gradient) {
        this.gradient = gradient;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
