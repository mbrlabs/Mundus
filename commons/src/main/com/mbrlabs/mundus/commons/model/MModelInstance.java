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

package com.mbrlabs.mundus.commons.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.mbrlabs.mundus.commons.assets.ModelAsset;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class MModelInstance {

    private ModelAsset model;
    public ModelInstance modelInstance = null;

    public MModelInstance(ModelAsset model) {
        this.model = model;
        if(model.getModel() != null) {
            modelInstance = new ModelInstance(model.getModel());
        }
    }

    public void replaceModel(ModelAsset model) {
        this.model = model;
        ModelInstance mi = new ModelInstance(model.getModel());
        mi.transform  = modelInstance.transform;
        modelInstance = mi;
    }

    public ModelAsset getModel() {
        return model;
    }

    public void setModel(ModelAsset model) {
        this.model = model;
    }

}
