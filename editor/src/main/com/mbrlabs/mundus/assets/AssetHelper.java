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

package com.mbrlabs.mundus.assets;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.MetaFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author Marcus Brummer
 * @version 02-10-2016
 */
public class AssetHelper {

    /**
     * Creates a new meta file and saves it at the given location.
     *
     * @param file  save location
     * @param type  asset type
     * @return      saved meta file
     * @throws IOException
     */
    public static MetaFile createNewMetaFile(FileHandle file, AssetType type) throws IOException {
        final MetaFile meta = new MetaFile(file);
        meta.setUuid(UUID.randomUUID().toString());
        meta.setVersion(MetaFile.CURRENT_VERSION);
        meta.setLastModified(new Date());
        meta.setType(type);
        meta.save();

        return meta;
    }

}
