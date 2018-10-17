#version 150

in vec3 pass_color;

out vec4 out_colour;

void main(void){
    out_colour = vec4(pass_color, 1);
}