layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;

layout (std140, binding = 3) uniform Fog {
    float fogDensity;
    float fogColorR;
    float fogColorG;
    float fogColorB;
};

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
    float sunColorR;
    float sunColorG;
    float sunColorB;
};

out vec2 texture_coordinates;
uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;
out vec3 mv_vertex_pos;

void main()
{
    mat4 model_view_matrix = view_matrix * model_matrix;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    mv_vertex_pos = mv_pos.xyz;
    gl_Position = projection_matrix * mv_pos;
    texture_coordinates = aTexture;
}
