#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 uv1 = texCoord.xy / InSize.xy;
    float valor = 1 - length(uv1);
    float linewidth = valor / 2000;

    vec2 uv = texCoord;
    vec2 uv_b = vec2(uv.x, uv.y + linewidth);
    vec2 uv_l = vec2(uv.x + linewidth, uv.y);
    vec2 uv_r = vec2(uv.x - linewidth, uv.y);
    vec2 uv_u = vec2(uv.x, uv.y - linewidth);

    vec4 center = texture(DiffuseSampler, texCoord.st);

    float near = 0.04;
    float far = 9.0;

    float d1 = 1 - 2 * near * far / (far + near - (2 * texture(DiffuseSampler, uv).x - 1) * (far - near)) / far;
    float d2 = 1 - 2 * near * far / (far + near - (2 * texture(DiffuseSampler, uv_b).x - 1) * (far - near)) / far;
    float d3 = 1 - 2 * near * far / (far + near - (2 * texture(DiffuseSampler, uv_u).x - 1) * (far - near)) / far;
    float d4 = 1 - 2 * near * far / (far + near - (2 * texture(DiffuseSampler, uv_l).x - 1) * (far - near)) / far;
    float d5 = 1 - 2 * near * far / (far + near - (2 * texture(DiffuseSampler, uv_r).x - 1) * (far - near)) / far;

    float factor = 20;
    float difference = (d1 - d2) * factor;
    float difference1 = (d1 - d3) * factor;
    float difference2 = (d1 - d4) * factor;
    float difference3 = (d1 - d5) * factor;
    float borders = clamp(difference + difference1 + difference2 + difference3, 0.0, 0.25) * 2.5;
    borders = sqrt(1.0 - pow(borders - 1.0, 2.0));

    fragColor = vec4(mix(texture(DiffuseSampler, uv).rgb, vec3(0.0, borders, 0.0), .85), texture(DiffuseSampler, uv).a);
}