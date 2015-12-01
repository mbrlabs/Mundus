#ifdef GL_ES
precision mediump float;
#endif

const vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);

varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;

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


    gl_FragColor = COLOR_TURQUOISE * vec4(brightness,brightness,brightness, 1.0);
}
