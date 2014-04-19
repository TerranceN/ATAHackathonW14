uniform sampler2D u_texture;
uniform float u_dt;
uniform float u_fadeRate;
uniform vec3 u_fadeRates;

varying vec2 v_texCoord;
varying vec4 v_color;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord) * v_color;
    gl_FragColor = vec4(
        max(0.0, texColor.r - u_fadeRates.r * u_dt),
        max(0.0, texColor.g - u_fadeRates.g * u_dt),
        max(0.0, texColor.b - u_fadeRates.b * u_dt), 1.0);
}
