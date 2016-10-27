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

import com.mbrlabs.mundus.commons.shaders.ModelShader;
import com.mbrlabs.mundus.commons.shaders.SkyboxShader;
import com.mbrlabs.mundus.commons.shaders.TerrainShader;

public class Shaders {

    private ModelShader modelShader;
    private TerrainShader terrainShader;
    private SkyboxShader skyboxShader;

    public Shaders() {
        modelShader = new ModelShader();
        modelShader.init();
        terrainShader = new TerrainShader();
        terrainShader.init();
        skyboxShader = new SkyboxShader();
    }

    public ModelShader getModelShader() {
        return modelShader;
    }

    public TerrainShader getTerrainShader() {
        return terrainShader;
    }

    public SkyboxShader getSkyboxShader() {
        return skyboxShader;
    }

}
