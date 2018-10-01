#version 330 core
layout (triangles) in;
layout (triangle_strip, max_vertices=18) out;

uniform mat4 shadowMatrices[6];

out vec4 FragPos; // FragPos from GS (output per emitvertex)

void main()
{
    for(gl_Layer=0; gl_Layer<6; ++gl_Layer) {
        for(int tri_vert=0; tri_vert<3; ++tri_vert) {
            FragPos = gl_in[tri_vert].gl_Position;
            gl_Position = shadowMatrices[gl_Layer] * FragPos;
            EmitVertex();
        }
        EndPrimitive();
    }
}