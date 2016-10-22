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

package com.mbrlabs.mundus.editor.shader

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.mbrlabs.mundus.commons.shaders.EntityShader
import com.mbrlabs.mundus.commons.skybox.SkyboxShader
import com.mbrlabs.mundus.commons.terrain.TerrainShader
import com.mbrlabs.mundus.editor.tools.picker.PickerShader

/**
 * @author Marcus Brummer
 * *
 * @version 08-12-2015
 */
object Shaders : Disposable {

    val wireframeShader: WireframeShader
    val terrainShader: TerrainShader
    val entityShader: EntityShader
    val skyboxShader: SkyboxShader
    val pickerShader: PickerShader

    init {
        ShaderProgram.pedantic = false
        wireframeShader = WireframeShader()
        wireframeShader.init()
        terrainShader = TerrainShader()
        terrainShader.init()
        entityShader = EntityShader()
        entityShader.init()
        skyboxShader = SkyboxShader()
        skyboxShader.init()
        pickerShader = PickerShader()
        pickerShader.init()
    }

    override fun dispose() {
        wireframeShader.dispose()
        terrainShader.dispose()
        entityShader.dispose()
        skyboxShader.dispose()
        pickerShader.dispose()
    }

}
