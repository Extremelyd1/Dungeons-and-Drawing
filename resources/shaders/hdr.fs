#version 330
out vec4 fragColor;

in vec2 TexCoords;

uniform sampler2D hdrTexture;

const float exposure = 1.2f;

void main()
{
    const float gamma = 0.7;    // Keep gamma 1.0 for colors sake
    // Lookup Color at the correct pixel
    vec3 hdrColor = texture(hdrTexture, TexCoords).rgb;
    // Compute the HDR color for it
    vec3 result = vec3(1.0) - exp(-hdrColor * exposure);
    // Gamma correction
    result = pow(result, vec3(1.0 / gamma));
    // Final Color
    fragColor = vec4(result, 1.0);

    // HDR OFF
    //result = pow(hdrColor, vec3(1.0 / gamma));
    //fragColor = vec4(result, 1.0);
}