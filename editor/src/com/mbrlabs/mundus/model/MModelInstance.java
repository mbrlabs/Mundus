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

package com.mbrlabs.mundus.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class MModelInstance {

    private long id;
    private long modelId;

    // used to store transformation that comes directly from the ModelInstanceDescriptor.
    // since the actual loading of the g3db model takes place after the deserialazion
    // i need to store the transformation somewhere else (here) until the model is loaded.
    // then this transformation can be applied to the actual model instance.
    public Matrix4 kryoTransform = new Matrix4();

    public ModelInstance modelInstance = null;

    public MModelInstance(MModel model) {
        modelId = model.id;
        if(model.getModel() != null) {
            modelInstance = new ModelInstance(model.getModel());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

}
