#version 330
layout (location = 0) in vec3 position;

out vec4 FragPos;
uniform mat4 modelMatrix;
uniform mat4 lightSpaceMatrix;

uniform int mode;

// Mode 0 related
const float offset = 1.0;
uniform float step;
uniform vec3 headPos;

// Mode 1 is default

void main() {
    vec3 pos;
    if (mode == 0) {
        float len = length(headPos - position);
        pos.x = position.x;
        pos.y = position.y;
        pos.z = position.z + offset * sin(step + len * 0.55f);
    } else {
        pos = position;
    }

    gl_Position = lightSpaceMatrix * modelMatrix * vec4(pos, 1.0f);   // Transform to light space
    FragPos = gl_Position;
}