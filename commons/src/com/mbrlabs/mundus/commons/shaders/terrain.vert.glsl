attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;

// lights
uniform vec3  u_lightPos;
uniform vec3  u_camPos;

varying vec2 v_texCoord0;
varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;
varying float v_fog;

const float FOG_DENSITY = 0.001;
const float FOG_GRADIENT = 1.5;

void main(void) {
    // position
    vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);
    gl_Position = u_projViewMatrix * worldPos;

    // for diffuse lighting
    v_surfaceNormal = (u_transMatrix * vec4(a_normal, 0.0)).xyz;
    v_vectorToLight = u_lightPos - worldPos.xyz;

    // texture coord
    v_texCoord0 = a_texCoord0;

    // fog intensity
    v_fog = distance(worldPos, vec4(u_camPos, 1.0));
    v_fog = exp(-pow(v_fog * FOG_DENSITY, FOG_GRADIENT));
    v_fog = 1.0 - clamp(v_fog, 0.0, 1.0);
}
