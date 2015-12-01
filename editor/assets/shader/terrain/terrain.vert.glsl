attribute vec3 a_position;
attribute vec3 a_normal;

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

    gl_Position = u_projViewMatrix * worldPos;
}
