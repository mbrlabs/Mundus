#ifdef GL_ES
precision mediump float;
#endif

uniform samplerCube u_environmentCubemap;
varying vec3 v_cubeMapUV;

void main() {
   // gl_FragColor = vec4(textureCube(u_environmentCubemap, v_cubeMapUV).rgb, 1.0);
   gl_FragColor = vec4(textureCube(u_environmentCubemap, v_cubeMapUV).rgb, 1.0);
}