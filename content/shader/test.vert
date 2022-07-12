#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aCol;
layout (location = 2) in vec2 aTex;


uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;



out vec4 col;
out vec2 tex_coords;


void main() {
    gl_Position = projection * view * model * vec4(aPos, 1.0);
    tex_coords = aTex;
    col = vec4(aCol,1.0);
}