#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 colors;
layout (location=3) in vec3 vertexNormal;

out VS_OUT {
    vec3 FragPos;
    vec3 Normal;
    vec2 TexCoords;
    vec4 Color;
} vs_out;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform mat4 projectionViewModel;

uniform int mode;

// Mode 0 related
const float offset = 1.0;
uniform float step;
uniform vec3 headPos;

// Mode 1 is default

void main()
{
    vec3 pos;
    if (mode == 0) {
        float len = length(headPos - position);
        pos.x = position.x;
        pos.y = position.y;
        pos.z = position.z + offset * sin(step + len * 0.55f);
    } else {
        pos = position;
    }

    vs_out.FragPos = vec3(model * vec4(pos, 1.0));
    vs_out.Normal  = transpose(inverse(mat3(model))) * vertexNormal;
    vs_out.TexCoords = texCoord;
    gl_Position = projectionViewModel * vec4(pos, 1.0);

    vs_out.Color = vec4(colors, 1);
}