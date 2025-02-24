#version 300 es
layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec2 a_TexCoord;
layout(location = 2) in vec3 a_Normal;

uniform mat4 u_MVPMatrix;
uniform mat4 u_ModelMatrix;
uniform mat3 u_NormalMatrix;

// Outputs to fragment shader
out vec2 v_TexCoord;
out vec3 v_Normal;
out vec3 v_FragPos;

void main() {
     gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
        v_TexCoord = a_TexCoord;

        // Properly transform normals
        v_Normal = normalize(u_NormalMatrix * a_Normal);

        // Transform fragment position to world space
        v_FragPos = vec3(u_ModelMatrix * vec4(a_Position, 1.0));
}
