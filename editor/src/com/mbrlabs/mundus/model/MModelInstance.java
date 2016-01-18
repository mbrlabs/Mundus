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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class MModelInstance {

    private long id;
    private long modelId;
    private boolean isSelected;

    public ModelInstance modelInstance = null;

    private final static BoundingBox bounds = new BoundingBox();
    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    public float radius;

    public MModelInstance(MModel model) {
        modelId = model.id;
        if(model.getModel() != null) {
            modelInstance = new ModelInstance(model.getModel());
        }
    }

    public void calculateBounds() {
        modelInstance.calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len() / 2f;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
