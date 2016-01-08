#ifdef GL_ES
precision mediump float;
#endif

uniform samplerCube u_texture;
varying vec3 v_cubeMapUV;

void main() {
   gl_FragColor = vec4(textureCube(u_texture, v_cubeMapUV).rgb, 1.0);
}