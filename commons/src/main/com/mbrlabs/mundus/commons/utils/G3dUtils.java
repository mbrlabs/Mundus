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


package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.g3d.MG3dModelLoader;

/**
 * @author Marcus Brummer
 * @version 02-07-2016
 */
public class G3dUtils {

    public static Model loadWithoutTextures(FileHandle model) {
        MG3dModelLoader loader = new MG3dModelLoader(new UBJsonReader());
        ModelData modelData = loader.loadModelData(model);
        for(ModelMaterial mat : modelData.materials) {
            mat.textures.clear();
        }

        return new Model(modelData);
    }

}
