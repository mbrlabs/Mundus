#ifdef GL_ES
precision mediump float;
#endif

const vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);

varying vec2 v_texCoord0;
varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;
varying float v_fog;

uniform sampler2D u_texture;
uniform vec4 u_fogColor;
varying vec4 v_lighting;

void main(void) {
    gl_FragColor = texture2D(u_texture, v_texCoord0);
    if(gl_FragColor.a < 0.5) {
        discard;
    }

    gl_FragColor *= v_lighting;
    gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);
}
