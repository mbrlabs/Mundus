attribute vec3 a_position;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;

void main(void) {

    vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);

    gl_Position = u_projViewMatrix * worldPos;
}
