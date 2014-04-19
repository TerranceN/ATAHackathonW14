uniform sampler2D u_texture;
uniform sampler2D u_maskTexture;

varying vec2 v_texCoord;
varying vec4 v_color;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord) * v_color;
    vec4 maskColor = texture2D(u_maskTexture, v_texCoord);
    gl_FragColor = vec4(texColor.xyz, (1.0 - maskColor.r) * texColor.w);
}
