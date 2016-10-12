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
uniform int u_texture_has_splatmap;
uniform int u_texture_has_diffuse;

uniform vec4 u_fogColor;

// light
varying vec4 v_lighting;
varying vec3 v_normal;

varying vec2 v_texCoord0;
varying float v_fog;

varying vec2 splatPosition;


// lights
struct DirectionalLight {
	vec4 color;
	vec3 direction;
	float intensity;
};
struct AmbientLight {
	vec4 color;
	float intensity;
};
uniform AmbientLight u_ambientLight;
uniform DirectionalLight u_directionalLight;


void main(void) {

    // blend textures
    if(u_texture_has_diffuse == 1) {
        gl_FragColor = texture2D(u_texture_base, v_texCoord0);
    }
    if(u_texture_has_splatmap == 1) {
        vec4 splat = texture2D(u_texture_splat, splatPosition);
        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_r, v_texCoord0), splat.r);
        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_g, v_texCoord0), splat.g);
        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_b, v_texCoord0), splat.b);
        gl_FragColor = mix(gl_FragColor, texture2D(u_texture_a, v_texCoord0), splat.a);
    }

    // =================================================================
    //                          Lighting
    // =================================================================
    vec4 diffuse_light = u_directionalLight.color
        * (dot(-u_directionalLight.direction, v_normal) * u_directionalLight.intensity);

    // ambient light
    diffuse_light += u_ambientLight.color * u_ambientLight.intensity;

    // =================================================================
    //                          /Lighting
    // =================================================================

    // lighting
    gl_FragColor *= diffuse_light;
    // fog
    gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);
}
