#ifdef GL_ES
precision mediump float;
#endif

const vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);
const vec4 COLOR_WHITE = vec4(1,1,1, 1.0);
const vec4 COLOR_DARK = vec4(0.05,0.05,0.05, 1.0);
const vec4 COLOR_BRIGHT = vec4(0.8,0.8,0.8, 1.0);

const vec4 COLOR_FOG = vec4(1 ,1, 1, 1.0);

uniform sampler2D u_texture;
uniform float u_lightIntensity;

varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;
varying vec2 v_texCoord0;
varying float v_fog;

void main(void) {
    vec3 unitNormal = normalize(v_surfaceNormal);
    vec3 unitLightVector = normalize(v_vectorToLight);
    float nDot1 = u_lightIntensity * dot(unitNormal, unitLightVector);
    // diffuse
    float brightness = max(nDot1, 0.2);

   // gl_FragColor = COLOR_DARK * vec4(brightness,brightness,brightness, 1.0);
    gl_FragColor = brightness * texture2D(u_texture, v_texCoord0);
   // gl_FragColor = mix(gl_FragColor, COLOR_FOG, v_fog);
}
