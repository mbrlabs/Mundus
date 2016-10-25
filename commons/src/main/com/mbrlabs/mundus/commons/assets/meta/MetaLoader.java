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

package com.mbrlabs.mundus.commons.assets.meta;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mbrlabs.mundus.commons.assets.AssetType;

/**
 *
 * @author Marcus Brummer
 * @version 26-10-2016
 */
public class MetaLoader {

    private final JsonReader reader = new JsonReader();

    public Meta load(FileHandle file) throws MetaFileParseException {
        Meta meta = new Meta(file);

        JsonValue json = reader.parse(file);
        parseBasics(meta, json);

        if(meta.getType() == AssetType.TERRAIN) {
            parseTerrain(meta, json.get(Meta.JSON_TERRAIN));
        } else if(meta.getType() == AssetType.MODEL) {
            parseModel(meta, json.get(Meta.JSON_MODEL));
        }

        return meta;
    }

    private void parseBasics(Meta meta, JsonValue jsonRoot) {
        meta.setVersion(jsonRoot.getInt(Meta.JSON_VERSION));
        meta.setLastModified(jsonRoot.getLong(Meta.JSON_LAST_MOD));
        meta.setUuid(jsonRoot.getString(Meta.JSON_UUID));
        meta.setType(AssetType.valueOf(jsonRoot.getString(Meta.JSON_TYPE)));
    }

    private void parseTerrain(Meta meta, JsonValue jsonTerrain) {
        final MetaTerrain terrain = new MetaTerrain();
        terrain.setSize(jsonTerrain.getInt(MetaTerrain.JSON_SIZE));
        terrain.setSplatmap(jsonTerrain.getString(MetaTerrain.JSON_SPLATMAP));
        terrain.setSplatBase(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_BASE));
        terrain.setSplatR(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_R));
        terrain.setSplatG(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_G));
        terrain.setSplatB(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_B));
        terrain.setSplatA(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_A));

        meta.setTerrain(terrain);
    }

    private void parseModel(Meta meta, JsonValue jsonModel) {
        final MetaModel model = new MetaModel();
        final JsonValue materials = jsonModel.get(MetaModel.JSON_DEFAULT_MATERIALS);

        for(JsonValue mat : materials) {
            final String g3dbID = mat.name;
            final String assetUUID = mat.getString(0);
            model.getDefaultMaterials().put(g3dbID, assetUUID);
        }

        meta.setModel(model);
    }

}
