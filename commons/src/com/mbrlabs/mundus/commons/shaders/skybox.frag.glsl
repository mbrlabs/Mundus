#ifdef GL_ES
precision mediump float;
#endif


uniform samplerCube u_texture;
uniform int u_fog;
uniform vec4 u_fogColor;

varying vec3 v_cubeMapUV;

const float lowerFogLimit = 0.0;
const float upperFogLimit = 0.3;

void main() {
    gl_FragColor = vec4(textureCube(u_texture, v_cubeMapUV).rgb, 1.0);

    if(u_fog == 1) {
       vec4 foggyFactor = (v_cubeMapUV.y - lowerFogLimit) / (upperFogLimit - lowerFogLimit);
       foggyFactor = clamp(foggyFactor, 0.0, 1.0);
       gl_FragColor = mix(u_fogColor, gl_FragColor, foggyFactor);
    }

}