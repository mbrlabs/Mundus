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

import com.badlogic.gdx.graphics.Camera

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.mbrlabs.mundus.commons.utils.ShaderUtils
import com.mbrlabs.mundus.editor.utils.GlUtils

private const val VERTEX_SHADER = "com/mbrlabs/mundus/editor/shader/wire.vert.glsl"
private const val FRAGMENT_SHADER = "com/mbrlabs/mundus/editor/shader/wire.frag.glsl"

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
class WireframeShader : BaseShader() {

    private val UNIFORM_PROJ_VIEW_MATRIX = register(BaseShader.Uniform("u_projViewMatrix"))
    private val UNIFORM_TRANS_MATRIX = register(BaseShader.Uniform("u_transMatrix"))

    init {
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, false)
    }

    override fun init() {
        super.init(program, null)
    }

    override fun compareTo(other: Shader): Int {
        return 0
    }

    override fun canRender(instance: Renderable): Boolean {
        return true
    }

    override fun begin(camera: Camera, context: RenderContext) {
        this.context = context
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
        this.context.setDepthMask(true)

        program.begin()

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined)
    }

    override fun render(renderable: Renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform)
        GlUtils.Unsafe.polygonModeWireframe()

        renderable.meshPart.render(program)
    }

    override fun end() {
        GlUtils.Unsafe.polygonModeFill()
        program.end()
    }

    override fun dispose() {
        program.dispose()
    }

}
