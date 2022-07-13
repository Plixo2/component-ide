#version 330 core

uniform sampler2D sampler1;

in vec4 col;
in vec2 tex_coords;


out vec4 outColor;

void main() {
//    FragColor = col;
//    outColor = texture(sampler1,tex_coords);
    outColor = vec4(1.0,0.0,0.4,1.0);
}