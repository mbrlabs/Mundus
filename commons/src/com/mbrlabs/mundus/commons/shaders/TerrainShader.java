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

package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.Env;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.SunLight;
import com.mbrlabs.mundus.commons.env.SunLightsAttribute;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class TerrainShader extends BaseShader {

    private static final String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/terrain.vert.glsl";
    private static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/terrain.frag.glsl";

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_LIGHT_POS = register(new Uniform("u_lightPos"));
    protected final int UNIFORM_CAM_POS = register(new Uniform("u_camPos"));
    protected final int UNIFORM_LIGHT_INTENSITY = register(new Uniform("u_lightIntensity"));
    protected final int UNIFORM_TEXTURE = register(new Uniform("u_texture"));

    protected final int UNIFORM_FOG_DENSITY = register(new Uniform("u_fogDensity"));
    protected final int UNIFORM_FOG_GRADIENT = register(new Uniform("u_fogGradient"));

    private ShaderProgram program;

    public TerrainShader() {
        super();
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, true);
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
        return instance.material == null;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.context = context;
        context.begin();
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        this.context.setDepthMask(true);

        program.begin();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
        set(UNIFORM_CAM_POS, camera.position);

    }

    @Override
    public void render(Renderable renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);

        // texture uniform
        TextureAttribute textureAttribute = ((TextureAttribute)(renderable.material.get(TextureAttribute.Diffuse)));
        if(textureAttribute != null) {
            set(UNIFORM_TEXTURE, textureAttribute.textureDescription.texture);
        }

        final SunLightsAttribute sla =
                renderable.environment.get(SunLightsAttribute.class, SunLightsAttribute.Type);
        final Array<SunLight> points = sla == null ? null : sla.lights;
        if(points != null && points.size > 0) {
            SunLight light = points.first();
            set(UNIFORM_LIGHT_POS, light.position);
            set(UNIFORM_LIGHT_INTENSITY, light.intensity);
        }

        // Fog
        Fog fog = ((Env)renderable.environment).getFog();
        if(fog == null) {
            set(UNIFORM_FOG_DENSITY, 0);
            set(UNIFORM_FOG_GRADIENT, 0);
        } else {
            set(UNIFORM_FOG_DENSITY, fog.density);
            set(UNIFORM_FOG_GRADIENT, fog.gradient);
        }

        // bind attributes, bind mesh & render; then unbinds everything
        renderable.meshPart.render(program);
    }


    @Override
    public void end() {
        context.end();
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }
}
