in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform sampler2D blur_sampler;

uniform sampler2D accumulated_alpha;
uniform sampler2D reveal_alpha;

vec4 hdr(vec4, float, float);

void main() {
    vec4 accum = texture(accumulated_alpha, out_texture);
    float reveal = texture(reveal_alpha, out_texture).r;

    vec3 accumColor = accum.rgb / max(accum.a, 1e-5f);
    float finalAlpha = 1.0 - reveal;

    vec4 mixedTransparency = vec4(accumColor, finalAlpha);
    vec4 sceneColor = texture(texture_sampler, out_texture) * (1.0 - mixedTransparency.a) + mixedTransparency * mixedTransparency.a;

    vec4 hdrSceneColor = hdr(sceneColor, 4.0, 0.3);

    frag_color = hdrSceneColor;
}

vec4 hdr(vec4 in_col, float exposure, float gamma) {
    vec3 rgb = in_col.rgb;
    vec4 blurSampler = texture(blur_sampler, out_texture);
    vec3 bl_c = blurSampler.rgb;
    rgb += bl_c * 1.;
    vec3 mapped = vec3(1.) - exp(-rgb * exposure);
    mapped = pow(mapped, vec3(1. / gamma));
    return vec4(mapped, in_col.a);
}