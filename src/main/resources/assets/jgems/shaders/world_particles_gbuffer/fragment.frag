in vec2 texture_coordinates;
in vec3 mv_vertex_pos;

uniform vec2 texture_scaling;
uniform sampler2D diffuse_map;
uniform float brightness;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec4 gEmission;

vec2 scaled_coordinates() {
    return texture_coordinates * texture_scaling;
}

void main()
{
    vec4 diffuse_texture = texture(diffuse_map, scaled_coordinates());
    gEmission = vec4(vec3(brightness), 1.);
    gNormal = vec4(vec3(0.), 1.);
    gPosition = vec4(mv_vertex_pos, 1.);
    gColor = diffuse_texture;
}