package com.mbrlabs.mundus.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.utils.ShaderUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class EntityShader extends BaseShader {

    private static final String VERTEX_SHADER = "shader/entity/entity.vert.glsl";
    private static final String FRAGMENT_SHADER = "shader/entity/entity.frag.glsl";

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_LIGHT_POS = register(new Uniform("u_lightPos"));
    protected final int UNIFORM_LIGHT_INTENSITY = register(new Uniform("u_lightIntensity"));

    protected final int UNIFORM_WIREFRAME = register(new Uniform("u_wireframe"));

    private ShaderProgram program;

    private int primitiveType = GL20.GL_TRIANGLES;

    public EntityShader() {
        super();
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public void toggleWireframe() {
        if(primitiveType == GL20.GL_TRIANGLES) {
            primitiveType = GL20.GL_LINES;
        } else {
            primitiveType = GL20.GL_TRIANGLES;
        }
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
        if(primitiveType == GL20.GL_TRIANGLES) {
            set(UNIFORM_WIREFRAME, 0);
        } else {
            set(UNIFORM_WIREFRAME, 1);
        }
    }

    @Override
    public void render(Renderable renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);

        final PointLightsAttribute pla =
                renderable.environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        renderable.meshPart.primitiveType = primitiveType;
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
