attribute vec4 a_Position;
attribute vec2 a_TexCoord;
attribute vec3 a_Normal;

uniform mat4 u_MVPMatrix;
uniform mat4 u_ModelMatrix;

varying vec2 v_TexCoord;
varying vec3 v_Normal;

void main() {
    gl_Position = u_MVPMatrix * a_Position;
    v_TexCoord = a_TexCoord;

    // Transform normal to world space
    v_Normal = normalize((u_ModelMatrix * vec4(a_Normal, 0.0)).xyz);
}
