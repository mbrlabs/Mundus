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
 * @version 26-10-2016
 */
public class ProjectSettingsDescriptor {

    @Tag(0)
    private boolean exportCompressScenes;
    @Tag(1)
    private boolean exportAllAssets;
    @Tag(2)
    private String exportOutputFolder;
    @Tag(3)
    private String jsonType;

    public String getJsonType() {
        return jsonType;
    }

    public void setJsonType(String jsonType) {
        this.jsonType = jsonType;
    }

    public boolean isExportCompressScenes() {
        return exportCompressScenes;
    }

    public void setExportCompressScenes(boolean exportCompressScenes) {
        this.exportCompressScenes = exportCompressScenes;
    }

    public boolean isExportAllAssets() {
        return exportAllAssets;
    }

    public void setExportAllAssets(boolean exportAllAssets) {
        this.exportAllAssets = exportAllAssets;
    }

    public String getExportOutputFolder() {
        return exportOutputFolder;
    }

    public void setExportOutputFolder(String exportOutputFolder) {
        this.exportOutputFolder = exportOutputFolder;
    }

}
