#ifdef GL_ES
precision mediump float;
#endif

const vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);
const vec4 COLOR_WHITE = vec4(1,1,1, 1.0);

varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;
varying vec2 v_texCoord0;

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
    float brightness = max(nDot1, 0.2);


//    gl_FragColor = COLOR_TURQUOISE * vec4(brightness,brightness,brightness, 1.0);
    gl_FragColor = brightness * texture2D(u_texture, v_texCoord0);

}
