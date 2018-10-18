#version 330

layout (location=0) in vec3 position;

out layout (location=10) in vec3 morphedPosition;

uniform float step;
uniform vec3 headPos;
uniform float rotation;
uniform mat4 projectionViewModel;

const int offset = 55;

void main()
{
    float len = length(headPos - position);
    morphedPosition.x = position.x + cos(rotation) * offset * sin(step + len * 0.1f);
    morphedPosition.y = position.y;
    morphedPosition.z = position.y + sin(rotation) * offset * sin(step + len * 0.1f);

    gl_Position = projectionViewModel * vec4(morphedPosition, 1.0);
}