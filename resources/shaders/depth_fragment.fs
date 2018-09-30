#version 330 core
in vec4 FragPos;

void main()
{
    gl_FragDepth = gl_FragCoord.z;
}