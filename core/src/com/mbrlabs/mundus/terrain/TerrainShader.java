package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class TerrainShader extends BaseShader {

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_LIGHT_POS = register(new Uniform("u_lightPos"));
    protected final int UNIFORM_LIGHT_INTENSITY = register(new Uniform("u_lightIntensity"));

    private ShaderProgram program;

    public TerrainShader() {
        super();
        String vert = Gdx.files.internal("shader/terrain.vert.glsl").readString();
        String frag = Gdx.files.internal("shader/terrain.frag.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
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
        context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        context.setDepthMask(true);
        program.begin();
        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
    }

    public void loadLight(PointLight light) {
        set(UNIFORM_LIGHT_POS, light.position);
        set(UNIFORM_LIGHT_INTENSITY, light.intensity);
    }

    @Override
    public void render(Renderable renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);
        final PointLightsAttribute pla =
                renderable.environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);

        final Array<PointLight> points = pla == null ? null : pla.lights;
        if(points != null && points.size > 0) {
            PointLight light = points.first();
            set(UNIFORM_LIGHT_POS, light.position);
            set(UNIFORM_LIGHT_INTENSITY, light.intensity);
        }

        // bind attributes, bind mesh & render; then unbinds everything
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
