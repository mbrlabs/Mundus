#ifdef GL_ES
precision mediump float;
#endif

const vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);
const vec4 COLOR_WHITE = vec4(1,1,1, 1.0);
const vec4 COLOR_DARK = vec4(0.05,0.05,0.05, 1.0);
const vec4 COLOR_BRIGHT = vec4(0.8,0.8,0.8, 1.0);

// textures
uniform sampler2D u_base_texture;

uniform sampler2D u_texture_r;
uniform sampler2D u_texture_g;
uniform sampler2D u_texture_b;
uniform sampler2D u_texture_a;
uniform sampler2D u_texture_blend;

// light
uniform float u_lightIntensity;
uniform vec4 u_fogColor;

varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;
varying vec2 v_texCoord0;
varying float v_fog;

varying vec3 pos;

vec4 blend_textures() {
    // base texture
    vec4 col = texture2D(u_base_texture, v_texCoord0);

    vec4 blend = texture2D(u_texture_blend, pos.xz);
    vec4 r = texture2D(u_texture_r, v_texCoord0);
    vec4 g = texture2D(u_texture_g, v_texCoord0);
   // vec4 b = texture2D(u_texture_b, v_texCoord0);
   // vec4 a = texture2D(u_texture_a, v_texCoord0);

    col = mix(col, r, blend.r);
    col = mix(col, g, blend.g);
  //  col = mix(col, b, blend.b);
   // col = mix(col, a, blend.a);

    return col;
}

void main(void) {
    vec3 unitNormal = normalize(v_surfaceNormal);
    vec3 unitLightVector = normalize(v_vectorToLight);
    float nDot1 = u_lightIntensity * dot(unitNormal, unitLightVector);
    // diffuse
    float brightness = max(nDot1, 0.2);

   // gl_FragColor = COLOR_DARK * vec4(brightness,brightness,brightness, 1.0);
    gl_FragColor = brightness * blend_textures();
    gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);

}
