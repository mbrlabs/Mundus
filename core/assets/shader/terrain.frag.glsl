#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord0;
varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;

uniform float u_lightIntensity;

void main(void) {

    vec3 unitNormal = normalize(v_surfaceNormal);
    vec3 unitLightVector = normalize(v_vectorToLight);
    float nDot1 = dot(unitNormal, unitLightVector);
    // diffuse
    float brightness = max(nDot1, 0.2);


    gl_FragColor = vec4(0,0,brightness, 1.0);
}
