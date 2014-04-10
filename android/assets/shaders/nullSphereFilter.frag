uniform sampler2D u_texture;
uniform sampler2D u_maskTexture;

varying vec2 v_texCoord;
varying vec4 v_color;

vec3 desaturate(vec3 color, float amount)
{
    vec3 gray = vec3(dot(vec3(0.2126,0.7152,0.0722), color));
    return vec3(mix(color, gray, amount));
}

void main() {
    vec4 finalColor = vec4(0, 0, 0, 0);
    vec4 texColor = texture2D(u_texture, v_texCoord) * v_color;
    vec4 maskColor = texture2D(u_maskTexture, v_texCoord);
    gl_FragColor = vec4(desaturate(texColor.xyz, maskColor.r * 0.75), 1.0);
}
