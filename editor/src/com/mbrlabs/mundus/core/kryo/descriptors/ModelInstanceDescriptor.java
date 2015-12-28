/*
 * Copyright (c) 2015. See AUTHORS file.
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

package com.mbrlabs.mundus.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class ModelInstanceDescriptor {

    @Tag(0)
    private long id;
    @Tag(1)
    private long modelID;

    @Tag(2)
    private float[] position = new float[3];
    @Tag(3)
    private float[] rotation = new float[3];
    @Tag(4)
    private float[] scale = new float[3];

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getModelID() {
        return modelID;
    }

    public void setModelID(long modelID) {
        this.modelID = modelID;
    }

    public float[] getPosition() {
        return position;
    }

    public float[] getRotation() {
        return rotation;
    }

    public float[] getScale() {
        return scale;
    }

}
