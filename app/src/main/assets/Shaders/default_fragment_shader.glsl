#version 300 es
precision mediump float;

// Inputs from the vertex shader
in vec2 v_TexCoord;
in vec3 v_Normal;
in vec3 v_FragPos;

// Uniforms for lighting and material properties
uniform sampler2D u_Texture;
uniform bool u_UseTexture;
uniform vec3 u_LightPos;
uniform vec3 u_LightColor;
uniform vec3 u_ViewPos;
uniform vec3 u_Color; // Tint color
uniform float u_TintStrength;

// Proper output declaration for OpenGL ES 3.0+
out vec4 FragColor;

void main() {
    if (u_UseTexture) {
      // Ambient lighting
          float ambientStrength = 0.2; // Increase ambient strength
          vec3 ambient = ambientStrength * u_LightColor;

          // Diffuse lighting
          vec3 norm = normalize(v_Normal);
          vec3 lightDir = normalize(u_LightPos - v_FragPos);
          float diff = max(dot(norm, lightDir), 0.0);
          vec3 diffuse = diff * u_LightColor;

          // Specular lighting (optional, adds shininess)
          float specularStrength = 0.5;
          vec3 viewDir = normalize(u_ViewPos - v_FragPos);
          vec3 reflectDir = reflect(-lightDir, norm);
          float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
          vec3 specular = specularStrength * spec * u_LightColor;

          // Combine lighting
          vec3 lighting = ambient + diffuse + specular;

          // Apply texture and tint
          vec4 baseColor = u_UseTexture ? texture(u_Texture, v_TexCoord) : vec4(1.0);
          vec3 tintedColor = mix(baseColor.rgb, u_Color, u_TintStrength);

          // Final color output with lighting
          FragColor = vec4(lighting * tintedColor, baseColor.a);
    } else {
        FragColor = vec4(u_Color, 1.0); // Use the solid color if no texture
    }
}
