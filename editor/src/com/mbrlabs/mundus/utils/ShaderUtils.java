package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * @author Marcus Brummer
 * @version 23-11-2015
 */
public class ShaderUtils {

    /**
     * Compiles and links shader.
     *
     * The shader source files must be in the assets folder.
     *
     * @param vertexShader      path to vertex shader
     * @param fragmentShader    path to fragment shader
     *
     * @return                  compiled shader program
     */
    public static ShaderProgram compile(String vertexShader, String fragmentShader) {
        String vert = Gdx.files.internal(vertexShader).readString();
        String frag = Gdx.files.internal(fragmentShader).readString();
        ShaderProgram program = new ShaderProgram(vert, frag);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }

        return program;
    }

}
