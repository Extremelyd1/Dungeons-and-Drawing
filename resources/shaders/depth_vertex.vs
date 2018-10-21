#version 330

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

layout (location = 0) in vec3 position;
// Mode 1 related
layout (location = 4) in ivec3 jointIndices;
layout (location = 5) in vec3 weights;

out vec4 FragPos;
uniform mat4 modelMatrix;
uniform mat4 lightSpaceMatrix;

uniform int mode;

// Mode 0 related
const float offset = 1.0;
uniform float step;
uniform vec3 headPos;

// Mode 1 related
uniform mat4 jointTransforms[MAX_JOINTS];

// Mode 99 is default

void main() {
    vec3 pos;
    if (mode == 0) {
        float len = length(headPos - position);
        pos.x = position.x;
        pos.y = position.y;
        pos.z = position.z + offset * sin(step + len * 0.55f);
    } else if (mode == 1) {
         vec4 totalLocalPos = vec4(0.0);

         for (int i = 0; i < MAX_WEIGHTS; i++) {
             mat4 jointTransform = jointTransforms[jointIndices[i]];
             vec4 posePosition = jointTransform * vec4(position, 1.0);
             totalLocalPos += posePosition * weights[i];
         }

         pos = vec3(totalLocalPos);
    } else {
        pos = position;
    }

    gl_Position = lightSpaceMatrix * modelMatrix * vec4(pos, 1.0f);   // Transform to light space
    FragPos = gl_Position;
}