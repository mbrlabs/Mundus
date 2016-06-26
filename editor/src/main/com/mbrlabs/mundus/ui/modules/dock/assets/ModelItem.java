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

package com.mbrlabs.mundus.ui.modules.dock.assets;

import com.mbrlabs.mundus.commons.model.MModel;

/**
 * @author Marcus Brummer
 * @version 26-06-2016
 */
public class ModelItem extends AssetItem {

    private MModel model;

    public ModelItem(String name, MModel model) {
        super(name, true);
        this.model = model;
    }

    public MModel getModel() {
        return model;
    }

    public void setModel(MModel model) {
        this.model = model;
    }

    @Override
    public void buildUi() {

    }

}
