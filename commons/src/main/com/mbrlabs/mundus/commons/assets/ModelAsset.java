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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.g3d.MG3dModelLoader;

/**
 * @author Marcus Brummer
 * @version 01-10-2016
 */
public class ModelAsset extends Asset {

    private Model model;

    public ModelAsset(MetaFile meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    public Model getModel() {
        return model;
    }

    public void setDiffuseTexture(Texture tex) {
        if(model == null) return;
        for(Material mat : model.materials) {
            TextureAttribute diffuse = new TextureAttribute(TextureAttribute.Diffuse, tex);
            mat.set(diffuse);
        }
    }

    @Override
    public void load() {
        // TODO don't create a new loader each time
        MG3dModelLoader loader = new MG3dModelLoader(new UBJsonReader());
        model = loader.loadModel(file);
        // TODO assign material values from meta file
    }

    @Override
    public void dispose() {
        if(model != null) {
            model.dispose();
        }
    }

}
