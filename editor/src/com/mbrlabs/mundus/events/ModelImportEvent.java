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

package com.mbrlabs.mundus.events;

import com.mbrlabs.mundus.model.MModel;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class ModelImportEvent {

    private MModel model;

    public ModelImportEvent(MModel model) {
        this.model = model;
    }

    public MModel getModel() {
        return model;
    }

    public void setModel(MModel model) {
        this.model = model;
    }

    public static interface ModelImportListener {
        @Subscribe
        public void onModelImported(ModelImportEvent importEvent);
    }

}
