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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.Env;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.SunLight;
import com.mbrlabs.mundus.commons.env.SunLightsAttribute;
import com.mbrlabs.mundus.commons.terrain.TerrainTextureSplat;
import com.mbrlabs.mundus.commons.terrain.TerrainTextureSplatAttribute;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class TerrainShader extends BaseShader {

    private static final String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/terrain.vert.glsl";
    private static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/terrain.frag.glsl";

    // ============================ MATRICES & CAM POSITION ============================
    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_CAM_POS = register(new Uniform("u_camPos"));

    // ============================ LIGHTS ============================
    protected final int UNIFORM_LIGHT_POS = register(new Uniform("u_lightPos"));
    protected final int UNIFORM_LIGHT_INTENSITY = register(new Uniform("u_lightIntensity"));

    // ============================ TEXTURE SPLATTING ============================
    protected final int UNIFORM_TERRAIN_SIZE = register(new Uniform("u_terrainSize"));
    protected final int UNIFORM_TEXTURE_BASE = register(new Uniform("u_texture_base"));
    protected final int UNIFORM_TEXTURE_R = register(new Uniform("u_texture_r"));
    protected final int UNIFORM_TEXTURE_G = register(new Uniform("u_texture_g"));
    protected final int UNIFORM_TEXTURE_B = register(new Uniform("u_texture_b"));
    protected final int UNIFORM_TEXTURE_A = register(new Uniform("u_texture_a"));
    protected final int UNIFORM_TEXTURE_SPLAT = register(new Uniform("u_texture_splat"));
    protected final int UNIFORM_TEXTURE_COUNT = register(new Uniform("u_texture_count"));

    // ============================ FOG ============================
    protected final int UNIFORM_FOG_DENSITY = register(new Uniform("u_fogDensity"));
    protected final int UNIFORM_FOG_GRADIENT = register(new Uniform("u_fogGradient"));
    protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"));

    private Vector2 terrainSize = new Vector2();

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
        return true;
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

        setTerrainSplatTextures(renderable);

        // light
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
            set(UNIFORM_FOG_DENSITY, 0f);
            set(UNIFORM_FOG_GRADIENT, 0f);
        } else {
            set(UNIFORM_FOG_DENSITY, fog.density);
            set(UNIFORM_FOG_GRADIENT, fog.gradient);
            set(UNIFORM_FOG_COLOR, fog.color);
        }

        // bind attributes, bind mesh & render; then unbinds everything
        renderable.meshPart.render(program);
    }

    private void setTerrainSplatTextures(Renderable renderable) {
        TerrainTextureSplatAttribute splatAttrib = (TerrainTextureSplatAttribute)
                renderable.material.get(TerrainTextureSplatAttribute.ATTRIBUTE_SPLAT0);
        TerrainTextureSplat splat = splatAttrib.splat;

        // set sampler2D uniforms
        int texCount = 0;
        setTilableTextureUniform(UNIFORM_TEXTURE_BASE, splat.base);
        texCount += setTilableTextureUniform(UNIFORM_TEXTURE_R, splat.chanR);
        texCount += setTilableTextureUniform(UNIFORM_TEXTURE_G, splat.chanG);
        texCount += setTilableTextureUniform(UNIFORM_TEXTURE_B, splat.chanB);
        texCount += setTilableTextureUniform(UNIFORM_TEXTURE_A, splat.chanA);
        setTilableTextureUniform(UNIFORM_TEXTURE_SPLAT, splat.splat);

        // set terrain world size
        terrainSize.x = splat.terrain.terrainWidth;
        terrainSize.y = splat.terrain.terrainDepth;
        set(UNIFORM_TERRAIN_SIZE, terrainSize);
        set(UNIFORM_TEXTURE_COUNT, texCount);
    }

    public int setTilableTextureUniform(int loc, Texture tex) {
        if(tex == null) return 0;
        set(loc, tex);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_REPEAT);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_REPEAT);

        return 1;
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
