uniform sampler2D u_texture;
uniform sampler2D u_heatTexture;
uniform sampler2D u_noiseTexture;

uniform float u_gameTime;
uniform vec2 u_noiseTexSize;

varying vec2 v_texCoord;
varying vec4 v_color;

void main() {
    float heatIntensity = texture2D(u_heatTexture, v_texCoord).r;
    float trail = heatIntensity / 8.0;
    vec2 noiseUV = gl_FragCoord / u_noiseTexSize;
    noiseUV = noiseUV / vec2(40.0, 20.0) + vec2(1, -1) * u_gameTime / 20.0;
    float noise = texture2D(u_noiseTexture, noiseUV).a;
    vec2 displacement = vec2(0, 1) * noise / 40.0;
    vec2 distortedCoord = v_texCoord + heatIntensity * displacement;
    gl_FragColor = texture2D(u_texture, distortedCoord) + (vec4(1, 0.25, 0.25, 1) * trail);
}
