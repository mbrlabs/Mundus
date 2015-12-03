package com.mbrlabs.mundus.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mbrlabs.mundus.utils.ShaderUtils;

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
public class BrushShader extends BaseShader {

    private static final String VERTEX_SHADER = "shader/brushes/brushes.vert.glsl";
    private static final String FRAGMENT_SHADER = "shader/brushes/brushes.frag.glsl";

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));

    private ShaderProgram program;

    private int primitiveType = GL20.GL_LINES;

    public BrushShader() {
        super();
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER);
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
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        this.context.setDepthMask(true);

        program.begin();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
    }

    @Override
    public void render(Renderable renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);
        renderable.meshPart.primitiveType = primitiveType;
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
