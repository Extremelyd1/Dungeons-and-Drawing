#version 330
layout (location = 0) in vec3 aPos;

out vec4 FragPos;
uniform mat4 modelMatrix;
uniform mat4 lightSpaceMatrix;

void main() {
    gl_Position = lightSpaceMatrix * modelMatrix * vec4(aPos, 1.0f);   // Transform to light space
    FragPos = gl_Position;
}