#ifdef GL_ES
precision mediump float;
#endif

const vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);
const vec4 COLOR_WHITE = vec4(1,1,1, 1.0);
const vec4 COLOR_DARK = vec4(0.05,0.05,0.05, 1.0);
const vec4 COLOR_BRIGHT = vec4(0.8,0.8,0.8, 1.0);

// splat textures
uniform sampler2D u_texture_base;
uniform sampler2D u_texture_r;
uniform sampler2D u_texture_g;
uniform sampler2D u_texture_b;
uniform sampler2D u_texture_a;
uniform sampler2D u_texture_splat;
uniform int u_texture_count;

// light
uniform float u_lightIntensity;
uniform vec4 u_fogColor;

varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;
varying vec2 v_texCoord0;
varying float v_fog;

varying vec2 splatPosition;

vec4 blend_textures() {
    // base texture
    vec4 col = texture2D(u_texture_base, v_texCoord0);

    if(u_texture_count > 1) {
        vec4 splat = texture2D(u_texture_splat, splatPosition);

        // r channel
        if(u_texture_count >= 2) {
            col = mix(col, texture2D(u_texture_r, v_texCoord0), splat.r);

            // g channel
            if(u_texture_count >= 3) {
                col = mix(col, texture2D(u_texture_g, v_texCoord0), splat.g);

                // b channel
                if(u_texture_count >= 4) {
                    col = mix(col, texture2D(u_texture_b, v_texCoord0), splat.b);

                   // a channel
                   if(u_texture_count == 5) {
                       col = mix(col, texture2D(u_texture_a, v_texCoord0), splat.a);
                   }
                }
            }
        }

    }

    return col;
}


void main(void) {
    float nDot1 = u_lightIntensity * dot(v_surfaceNormal, v_vectorToLight);
    float brightness = max(nDot1, 0.2);

    gl_FragColor = brightness * blend_textures();
    // add fog
    gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);

}
