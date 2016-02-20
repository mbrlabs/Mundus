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

package com.mbrlabs.mundus.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mbrlabs.mundus.utils.GlUtils;

/**
 * @author Marcus Brummer
 * @version 20-02-2016
 */
public class RaypickShader extends BaseShader {

    private static final String VERTEX_SHADER =
            "attribute vec3 a_position;" +
            "uniform mat4 u_transMatrix;" +
            "uniform mat4 u_projViewMatrix;" +
            "void main(void) {" +
                "vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);" +
                "gl_Position = u_projViewMatrix * worldPos;" +
            "}";


    private static final String FRAGMENT_SHADER =
            "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif \n" +
            "uniform vec4 u_color;" +
            "void main(void) {" +
                "gl_FragColor = u_color;" +
            "}";

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));

    protected final int UNIFORM_COLOR = register(new Uniform("u_color"));


    private ShaderProgram program;

    public RaypickShader() {
        super();
        program = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void init() {
        super.init(program, null);
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.context = context;
        this.context.setCullFace(GL20.GL_BACK);
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        this.context.setDepthMask(true);

        program.begin();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
    }

    @Override
    public void render(Renderable renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);

        ColorAttribute color = (ColorAttribute) renderable.material.get(ColorAttribute.Diffuse);
        set(UNIFORM_COLOR, color.color);

        renderable.meshPart.render(program);
    }

    @Override
    public void end() {
        GlUtils.Unsafe.polygonModeFill();
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }

}
