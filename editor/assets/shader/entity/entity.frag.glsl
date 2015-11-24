#ifdef GL_ES
precision mediump float;
#endif

const vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);
const vec3 LIGHT_COLOR = vec3(1,1,1);

varying vec2 v_texCoord0;
varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;

uniform sampler2D u_texture;

uniform float u_lightIntensity;
uniform int u_wireframe;

void main(void) {
    // Wireframe mode
    if(u_wireframe == 1) {
        gl_FragColor = COLOR_TURQUOISE;
        return;
    }
    vec3 unitNormal = normalize(v_surfaceNormal);
    vec3 unitLightVector = normalize(v_vectorToLight);
    float nDot1 = u_lightIntensity * dot(unitNormal, unitLightVector);
    // diffuse
    float brightness = u_lightIntensity * max(nDot1, 0.2);

    vec3 light = brightness * LIGHT_COLOR;

    vec4 color =  texture(u_texture, v_texCoord0);


    gl_FragColor = color;
}
