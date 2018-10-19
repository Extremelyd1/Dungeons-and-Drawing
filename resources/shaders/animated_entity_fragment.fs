#version 150

in vec2 pass_textureCoords;
in vec3 pass_normal;

uniform sampler2D diffuseMap;
out vec4 out_colour;

void main(void) {
    vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);
	//vec3 unitNormal = normalize(pass_normal);
	//float diffuseLight = max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y;
	out_colour = diffuseColour;
	//out_colour = vec4(100, 100, 100, 1);
}