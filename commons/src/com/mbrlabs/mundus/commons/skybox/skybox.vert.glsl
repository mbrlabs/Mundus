attribute vec3 a_position;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;

varying vec3 v_cubeMapUV;

void main() {
    gl_Position = u_projViewMatrix * u_transMatrix * vec4(a_position, 1.0);
    v_cubeMapUV = a_position;
}