/*
 * Copyright (c) 2015. See AUTHORS file.
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

package com.mbrlabs.mundus.shader;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mbrlabs.mundus.commons.shaders.EntityShader;
import com.mbrlabs.mundus.commons.skybox.SkyboxShader;
import com.mbrlabs.mundus.terrain.TerrainShader;
import com.mbrlabs.mundus.tools.picker.GameObjectPickerShader;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class Shaders {

    public WireframeShader wireframeShader;
    public TerrainShader terrainShader;
    public EntityShader entityShader;
    public SkyboxShader skyboxShader;
    public static GameObjectPickerShader gameObjectPickerShader;

    public Shaders() {
        ShaderProgram.pedantic = false;
        wireframeShader = new WireframeShader();
        wireframeShader.init();
        terrainShader = new TerrainShader();
        terrainShader.init();
        entityShader = new EntityShader();
        entityShader.init();
        skyboxShader = new SkyboxShader();
        skyboxShader.init();
        gameObjectPickerShader = new GameObjectPickerShader();
        gameObjectPickerShader.init();
    }

    public void dispose() {
        wireframeShader.dispose();
        terrainShader.dispose();
        entityShader.dispose();
        skyboxShader.dispose();
    }

}
