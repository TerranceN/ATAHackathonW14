attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_color;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    v_color = a_color;
    v_texCoord = a_texCoord0;
    vec4 finishedPosition = u_projTrans * a_position;
    gl_Position = finishedPosition;
}
