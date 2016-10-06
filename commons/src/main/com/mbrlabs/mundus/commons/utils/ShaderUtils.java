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

package com.mbrlabs.mundus.commons.utils;

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
     * @param vertexShader
     *            path to vertex shader
     * @param fragmentShader
     *            path to fragment shader
     * @param classpath
     *            true if shader in classpath false if shader in assets folder
     *
     * @return compiled shader program
     */
    public static ShaderProgram compile(String vertexShader, String fragmentShader, boolean classpath) {
        String vert;
        String frag;
        if (classpath) {
            vert = Gdx.files.classpath(vertexShader).readString();
            frag = Gdx.files.classpath(fragmentShader).readString();
        } else {
            vert = Gdx.files.internal(vertexShader).readString();
            frag = Gdx.files.internal(fragmentShader).readString();
        }

        ShaderProgram program = new ShaderProgram(vert, frag);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }

        return program;
    }

}
