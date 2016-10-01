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
import com.badlogic.gdx.utils.Disposable;

import java.util.UUID;

/**
 * @author Marcus Brummer
 * @version 01-10-2016
 */
public abstract class Asset implements Disposable {

    protected FileHandle file;
    protected MetaFile meta;

    public Asset(FileHandle file, String uuid) {
        this.meta = new MetaFile(new FileHandle(file.path()));
        this.meta.setUuid(uuid);
        this.file = file;
    }

    public Asset(FileHandle file) {
        this(file, UUID.randomUUID().toString());
    }

    public MetaFile getMeta() {
        return meta;
    }

    public abstract void load();

}
