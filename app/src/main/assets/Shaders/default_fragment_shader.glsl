precision mediump float;

uniform sampler2D u_Texture;
uniform bool u_UseTexture;

varying vec2 v_TexCoord;

void main() {
    vec4 baseColor;

    // Use texture if enabled, otherwise use solid color
  // if (u_UseTexture) {
    //    baseColor = texture2D(u_Texture, v_TexCoord);
   // } else {
        baseColor = vec4(1.0, 0.5, 0.3, 1.0); // Solid orange color
   // }

    // Direct color output
    gl_FragColor = baseColor;
}
