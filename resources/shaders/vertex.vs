#version 330

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 colors;
layout (location=3) in vec3 vertexNormal;
// Mode 1 related
layout (location=4) in ivec3 jointIndices;
layout (location=5) in vec3 weights;

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

// Mode 1 related
uniform mat4 jointTransforms[MAX_JOINTS];

uniform int mode;

// Mode 0 related
const float offset = 1.0;
uniform float step;
uniform vec3 headPos;

// Mode 99 is default

void main()
{
    vec3 pos;
    vec3 normal;
    if (mode == 0) {
        float len = length(headPos - position);
        pos.x = position.x;
        pos.y = position.y;
        pos.z = position.z + offset * sin(step + len * 0.55f);
        normal = vertexNormal;
    } else if (mode == 1) {
        vec4 totalLocalPos = vec4(0.0);
        vec4 totalNormal = vec4(0.0);

        for (int i = 0; i < MAX_WEIGHTS; i++) {
            mat4 jointTransform = jointTransforms[jointIndices[i]];
            vec4 posePosition = jointTransform * vec4(position, 1.0);
            totalLocalPos += posePosition * weights[i];

            vec4 worldNormal = jointTransform * vec4(vertexNormal, 0.0);
            totalNormal += worldNormal * weights[i];
        }

        pos = vec3(totalLocalPos);
        normal = vec3(totalNormal);
    } else {
        pos = position;
        normal = vertexNormal;
    }

    vs_out.FragPos = vec3(model * vec4(pos, 1.0));
    vs_out.Normal  = transpose(inverse(mat3(model))) * normal;
    vs_out.TexCoords = texCoord;
    gl_Position = projectionViewModel * vec4(pos, 1.0);

    vs_out.Color = vec4(colors, 1);
}