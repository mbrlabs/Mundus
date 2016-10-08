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

package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

/**
 * A generic asset type.
 *
 * Assets hold a file handle to the asset file. They also have a meta file,
 * which contains meta information about the asset.
 * 
 * @author Marcus Brummer
 * @version 01-10-2016
 */
public abstract class Asset implements Disposable {

    protected FileHandle file;
    private MetaFile meta;

    /**
     *
     * @param meta
     * @param assetFile
     */
    public Asset(MetaFile meta, FileHandle assetFile) {
        this.meta = meta;
        this.file = assetFile;
    }

    public MetaFile getMeta() {
        return meta;
    }

    public String getName() {
        return file.name();
    }

    public FileHandle getFile() {
        return file;
    }

    public String getUUID() {
        return meta.getID();
    }

    @Override
    public String toString() {
        return "[" + meta.getType().toString() + "] " + file.name();
    }

    /**
     * Loads the asset.
     *
     * Loads the asset from disk and creates it.
     */
    public abstract void load();

    /**
     * Applies dependent assets to this one.
     *
     * If dependencies have been set, this method applies them to the asset.
     * Note, that the model and all it's set dependencies must have called
     * load() before calling this method.
     */
    public abstract void applyDependencies();

}
