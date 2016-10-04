#ifdef GL_ES
precision mediump float;
#endif

const vec4 COLOR_TURQUOISE = vec4(0,0.714,0.586, 1.0);

varying vec2 v_texCoord0;
varying vec3 v_vectorToLight;
varying vec3 v_surfaceNormal;
varying float v_fog;
varying vec4 v_lighting;

// diffuse material
uniform sampler2D u_diffuseTexture;
uniform vec4 u_diffuseColor;
uniform int u_diffuseUseTexture;

// enviroment
uniform vec4 u_fogColor;

void main(void) {
    if(u_diffuseUseTexture == 1) {
        gl_FragColor = texture2D(u_diffuseTexture, v_texCoord0);
        //    if(gl_FragColor.a < 0.5) {
        //        discard;
        //    }
    } else {
        gl_FragColor = u_diffuseColor;
    }

    gl_FragColor *= v_lighting;
    gl_FragColor = mix(gl_FragColor, u_fogColor, v_fog);
}
