#version 330
layout (location = 0) in vec3 aPos;

uniform mat4 shadowMatrice;
uniform mat4 modelMatrix;

out vec4 FragPos;

void main() {
    FragPos = modelMatrix * vec4(aPos, 1.0);
    gl_Position = shadowMatrice * FragPos;   // Transform to world space
}