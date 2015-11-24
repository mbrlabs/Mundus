attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;

// lights
uniform vec3  u_lightPos;

varying vec2 v_texCoord0;
varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;

void main(void) {
    vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);

    // for diffuse lighting
    v_surfaceNormal = (u_transMatrix * vec4(a_normal, 0.0)).xyz;
    v_vectorToLight = u_lightPos - worldPos.xyz;

    v_texCoord0 = a_texCoord0;
    gl_Position = u_projViewMatrix * worldPos;
}
