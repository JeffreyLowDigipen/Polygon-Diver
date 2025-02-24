#version 300 es
precision mediump float;

in vec2 v_TexCoord;

uniform sampler2D u_Texture;
uniform bool u_UseTexture;
uniform vec3 u_Color;       // Base color or tint color
uniform float u_TintStrength; // Tint strength from 0.0 (no tint) to 1.0 (full tint)

out vec4 fragColor;

void main() {
    if (u_UseTexture) {
        vec4 texColor = texture(u_Texture, v_TexCoord);
        // Blend texture color with tint color
        fragColor = mix(texColor, vec4(u_Color, 1.0), u_TintStrength);
    } else {
        fragColor = vec4(u_Color, 1.0); // Use the solid color if no texture
    }
}
