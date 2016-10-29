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

package com.mbrlabs.mundus.editor.tools

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Disposable
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.shader.Shaders

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
abstract class Tool(protected var projectManager: ProjectManager, protected var batch: ModelBatch,
                    protected var history: CommandHistory) : InputAdapter(), Disposable {

    protected var shader: Shader

    init {
        shader = Shaders.wireframeShader
    }

    abstract val name: String
    abstract val icon: Drawable
    abstract val iconFont: String

    abstract fun render()
    abstract fun act()
    abstract fun onActivated()
    abstract fun onDisabled()

}
