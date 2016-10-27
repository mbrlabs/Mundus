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


uniform samplerCube u_texture;
uniform int u_fog;
uniform vec4 u_fogColor;

varying vec3 v_cubeMapUV;

const float lowerFogLimit = 0.0;
const float upperFogLimit = 0.1;

void main() {
    gl_FragColor = vec4(textureCube(u_texture, v_cubeMapUV).rgb, 1.0);

    if(u_fog == 1) {
       float foggyFactor = (v_cubeMapUV.y - lowerFogLimit) / (upperFogLimit - lowerFogLimit);
       foggyFactor = clamp(foggyFactor, 0.0, 1.0);
       gl_FragColor = mix(u_fogColor, gl_FragColor, foggyFactor);
    }

}