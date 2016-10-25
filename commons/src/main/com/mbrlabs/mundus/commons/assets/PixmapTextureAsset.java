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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.assets.meta.Meta;

import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 02-10-2016
 */
public class PixmapTextureAsset extends Asset {

    private Pixmap pixmap;
    private Texture texture;

    public PixmapTextureAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    public Pixmap getPixmap() {
        return pixmap;
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public void load() {
        pixmap = new Pixmap(file);
        texture = new Texture(pixmap);
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        // no dependencies here
    }

    @Override
    public void applyDependencies() {
        // no dependencies here
    }

    @Override
    public void dispose() {
        pixmap.dispose();
        texture.dispose();
    }

}
