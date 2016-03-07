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

package com.mbrlabs.mundus.tools.picker;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.tools.picker.PickerIDAttribute;

/**
 * Used to render game objects in only one color.
 *
 * This color represents the encoded id of the game object.
 * By rendering with this shader on a framebuffer object one can implement raypicking.
 * The class GameObjectPicker does exactly that.
 *
 * See also: http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/
 *
 * @author Marcus Brummer
 * @version 20-02-2016
 */
public class PickerShader extends BaseShader {

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
            "uniform vec3 u_color;" +
            "void main(void) {" +
                "gl_FragColor = vec4(u_color.r/255.0, u_color.g/255.0, u_color.b/255.0, 1.0);" +
            "}";

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));

    protected final int UNIFORM_COLOR = register(new Uniform("u_color"));

    private static Vector3 vec3 = new Vector3();


    private ShaderProgram program;

    public PickerShader() {
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

        PickerIDAttribute goID = (PickerIDAttribute) renderable.material.get(PickerIDAttribute.Type);
        if(goID != null) {
            set(UNIFORM_COLOR, vec3.set(goID.r, goID.g, goID.b));
        }

        renderable.meshPart.render(program);
    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }

}
