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

package com.mbrlabs.mundus.runtime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.AssetManager;

/**
 * @author Marcus Brummer
 * @version 27-10-2016
 */
public class Mundus implements Disposable {

    private static final String TAG = Mundus.class.getSimpleName();

    private final AssetManager assetManager;
    private final FileHandle root;

    public Mundus(final FileHandle mundusRoot) {
        this.root = mundusRoot;
        this.assetManager = new AssetManager(root.child("assets"));
    }

    public void init() {
        try {
            assetManager.loadAssets(null);
        } catch (Exception e) {
            Gdx.app.log(TAG, e.getMessage());
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Scene loadScene(final String name) {
        Scene scene = null;
        // TODO implement
        return scene;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

}
