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

package com.mbrlabs.mundus.editor.assets

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.mbrlabs.mundus.commons.assets.AssetType
import com.mbrlabs.mundus.commons.assets.meta.Meta
import com.mbrlabs.mundus.commons.assets.meta.MetaModel
import com.mbrlabs.mundus.commons.assets.meta.MetaTerrain

/**
 *
 */
class MetaSaver {

    fun save(meta: Meta) {
        val json = Json(JsonWriter.OutputType.json)
        json.setWriter(meta.file.writer(false))

        json.writeObjectStart()
        addBasics(meta, json)
        if(meta.type == AssetType.TERRAIN) {
            addTerrain(meta, json)
        } else if(meta.type == AssetType.MODEL) {
            addModel(meta, json)
        }
        json.writeObjectEnd()

        json.writer.flush()
    }

    private fun addBasics(meta: Meta, json: Json) {
        json.writeValue(Meta.JSON_VERSION, meta.version)
        json.writeValue(Meta.JSON_LAST_MOD, meta.lastModified)
        json.writeValue(Meta.JSON_TYPE, meta.type)
        json.writeValue(Meta.JSON_UUID, meta.uuid)
    }

    private fun addModel(meta: Meta, json: Json) {
        val model = meta.model ?: return
        json.writeObjectStart(Meta.JSON_MODEL)

        // default materials

        if(model.defaultMaterials != null) {
            json.writeObjectStart(MetaModel.JSON_DEFAULT_MATERIALS)
            for (mat in model.defaultMaterials) {
                json.writeValue(mat.key, mat.value)
            }
            json.writeObjectEnd()
        }

        json.writeObjectEnd()
    }

    private fun addTerrain(meta: Meta, json: Json) {
        val terrain = meta.terrain ?:return

        json.writeObjectStart(Meta.JSON_TERRAIN)
        json.writeValue(MetaTerrain.JSON_SIZE, terrain.size)
        if (terrain.splatmap != null) json.writeValue(MetaTerrain.JSON_SPLATMAP, terrain.splatmap)
        if (terrain.splatBase != null) json.writeValue(MetaTerrain.JSON_SPLAT_BASE, terrain.splatBase)
        if (terrain.splatR != null) json.writeValue(MetaTerrain.JSON_SPLAT_R, terrain.splatR)
        if (terrain.splatG != null) json.writeValue(MetaTerrain.JSON_SPLAT_G, terrain.splatG)
        if (terrain.splatB != null) json.writeValue(MetaTerrain.JSON_SPLAT_B, terrain.splatB)
        if (terrain.splatA != null) json.writeValue(MetaTerrain.JSON_SPLAT_A, terrain.splatA)
        json.writeObjectEnd()
    }


}