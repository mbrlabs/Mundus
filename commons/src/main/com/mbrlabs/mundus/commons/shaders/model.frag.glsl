/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifdef GL_ES
precision mediump float;
#endif

const vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);
const vec4 AMBIENT = vec4(0.05,0.05,0.05,0.05);

varying vec2    v_texCoord0;
varying vec3    v_vectorToLight;
varying vec3    v_surfaceNormal;
varying float   v_fog;
varying vec4    v_lighting;

// diffuse material
uniform sampler2D   u_diffuseTexture;
uniform vec4        u_diffuseColor;
uniform int         u_diffuseUseTexture;

// enviroment
uniform vec4 u_fogColor;

void main(void) {
    if(u_diffuseUseTexture == 1) {
        gl_FragColor = texture2D(u_diffuseTexture, v_texCoord0);
        //    if(gl_FragColor.a < 0.5) {
        //        discard;
        //    }
    } else {
        gl_FragColor = u_diffuseColor;
    }

    gl_FragColor = max(gl_FragColor, AMBIENT); // TODO make ambient color a unifrom
    gl_FragColor *= v_lighting;
    gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);
}
