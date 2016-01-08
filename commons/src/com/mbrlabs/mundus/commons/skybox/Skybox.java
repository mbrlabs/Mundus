/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.commons.skybox;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author Marcus Brummer
 * @version 08-01-2016
 */
public class Skybox implements Disposable {

    protected ShaderProgram shader;

    private Model boxModel;
    private ModelInstance boxInstance;

    private Cubemap cubemap;

    public Skybox(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY,
                  FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ) {
        cubemap = new Cubemap(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);

        boxModel = createModel();
        boxInstance = new ModelInstance(boxModel);
    }

    private Model createModel(){
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(1, 1, 1,
                new Material(new CubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap)),
                VertexAttributes.Usage.Position);
        return model;
    }

    public ModelInstance getSkyboxInstance() {
        return boxInstance;
    }

    @Override
    public void dispose() {
        boxModel.dispose();
        cubemap.dispose();
    }

}
