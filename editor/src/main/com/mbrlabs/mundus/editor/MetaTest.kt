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

package com.mbrlabs.mundus.editor

import com.badlogic.gdx.files.FileHandle
import com.mbrlabs.mundus.commons.assets.AssetType
import com.mbrlabs.mundus.commons.assets.meta.Meta
import com.mbrlabs.mundus.commons.assets.meta.MetaLoader
import com.mbrlabs.mundus.commons.assets.meta.MetaTerrain
import com.mbrlabs.mundus.editor.assets.MetaSaver
import java.util.*

fun metaTest() {
    // save
    val meta = Meta(FileHandle("/home/marcus/Desktop/test.meta"))
    meta.version = 1
    meta.lastModified = System.currentTimeMillis()
    meta.type = AssetType.TERRAIN
    meta.uuid = UUID.randomUUID().toString().replace("-", "")
  //  meta.terrain = MetaTerrain()
 //   meta.terrain.size = 800
//    meta.terrain.splatBase = UUID.randomUUID().toString().replace("-", "")
//    meta.terrain.splatmap = UUID.randomUUID().toString().replace("-", "")
//    meta.terrain.splatR = UUID.randomUUID().toString().replace("-", "")
//    meta.terrain.splatG = UUID.randomUUID().toString().replace("-", "")
//    meta.terrain.splatB = UUID.randomUUID().toString().replace("-", "")
//    meta.terrain.splatA = UUID.randomUUID().toString().replace("-", "")

    val saver = MetaSaver()
    saver.save(meta)

    // load
    val loader = MetaLoader()
    val meta2 = loader.load(meta.file)
    println(meta2)

}