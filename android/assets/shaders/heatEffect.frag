uniform sampler2D u_texture;
uniform sampler2D u_heatTexture;
uniform sampler2D u_noiseTexture;

uniform float u_gameTime;
uniform vec2 u_noiseTexSize;

uniform float u_backgroundHeat;

varying vec2 v_texCoord;
varying vec4 v_color;

void main() {
    vec4 heatData = texture2D(u_heatTexture, v_texCoord);
    float heatIntensity = heatData.r + (heatData.b + u_backgroundHeat) / 4.0;
    float trail = (heatData.g + heatData.b + u_backgroundHeat) / 2.0;
    vec2 noiseUV = gl_FragCoord.xy / u_noiseTexSize;
    noiseUV = noiseUV / (vec2(2.0, 1.0) * 10.0) + vec2(1, -1) * u_gameTime / 7.0;
    float noise = texture2D(u_noiseTexture, noiseUV).a;
    vec2 displacement = vec2(0, 1) * noise / 30.0;
    vec2 distortedCoord = v_texCoord + heatIntensity * displacement;
    gl_FragColor = texture2D(u_texture, distortedCoord) + (vec4(1, 0.1, 0.1, 1) * trail);
}
