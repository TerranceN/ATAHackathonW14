uniform sampler2D u_texture;

varying vec2 v_texCoord;
varying vec4 v_color;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord) * v_color;
    gl_FragColor = vec4(max(0.0, texColor.r - 0.01), 0.0, 0.0, 1.0);
}
