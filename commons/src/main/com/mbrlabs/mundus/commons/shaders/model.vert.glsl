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

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;
uniform vec3 u_camPos;

// lights
struct DirectionalLight {
	vec4 color;
	vec3 direction;
    float intensity;
};
struct AmbientLight {
	vec4 color;
	float intensity;
};
uniform AmbientLight        u_ambientLight;
uniform DirectionalLight    u_directionalLight;

uniform float u_shininess;

// Fog
uniform float  u_fogDensity;
uniform float  u_fogGradient;

varying vec2    v_texCoord0;
varying float   v_fog;
varying vec4    v_lighting;

void main(void) {
    vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);
    v_texCoord0 = a_texCoord0;
    gl_Position = u_projViewMatrix * worldPos;

    // =================================================================
    //                          Lighting
    // =================================================================
    vec3 normal = normalize((u_transMatrix * vec4(a_normal, 0.0)).xyz);

    // diffuse light
    v_lighting = u_directionalLight.color
        * (dot(-u_directionalLight.direction, normal) * u_directionalLight.intensity);

    // specular light
    vec3 vertexToCam = normalize(u_camPos - worldPos.xyz);
    vec3 reflectedLightDirection = reflect(u_directionalLight.direction, normal);
    float specularity = max(dot(reflectedLightDirection, vertexToCam), 0.0) * u_shininess;
    v_lighting += specularity * u_directionalLight.color * u_directionalLight.intensity;

    // ambient light
    v_lighting += u_ambientLight.color * u_ambientLight.intensity;

    // =================================================================
    //                          /Lighting
    // =================================================================

    // fog intensity
    if(u_fogDensity > 0.0 && u_fogGradient > 0.0) {
        v_fog = distance(worldPos, vec4(u_camPos, 1.0));
        v_fog = exp(-pow(v_fog * u_fogDensity, u_fogGradient));
        v_fog = 1.0 - clamp(v_fog, 0.0, 1.0);
    } else {
        v_fog = 0.0;
    }
}
