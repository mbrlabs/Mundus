attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;
uniform vec3 u_camPos;

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

// Fog
uniform float  u_fogDensity;
uniform float  u_fogGradient;

uniform vec2 u_terrainSize;
uniform int u_texture_has_splatmap;

varying vec2 v_texCoord0;
varying vec2 splatPosition;
varying float v_fog;
varying vec4 v_lighting;

void main(void) {

    // position
    vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);
    gl_Position = u_projViewMatrix * worldPos;

    // =================================================================
    //                          Lighting
    // =================================================================
    v_lighting = u_directionalLight.color * dot(a_normal, u_directionalLight.direction) * u_directionalLight.intensity;

    // ambient light
    v_lighting += u_ambientLight.color * u_ambientLight.intensity;

    // =================================================================
    //                          /Lighting
    // =================================================================

    // texture stuff
    v_texCoord0 = a_texCoord0;
    splatPosition = vec2(a_position.x / u_terrainSize.x, a_position.z / u_terrainSize);

    // fog
    if(u_fogDensity > 0.0 && u_fogGradient > 0.0) {
        v_fog = distance(worldPos, vec4(u_camPos, 1.0));
        v_fog = exp(-pow(v_fog * u_fogDensity, u_fogGradient));
        v_fog = 1.0 - clamp(v_fog, 0.0, 1.0);
    } else {
        v_fog = 0.0;
    }

}
