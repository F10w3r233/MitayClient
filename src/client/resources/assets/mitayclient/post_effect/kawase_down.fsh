#version 330

#moj_import <minecraft:globals.glsl>

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo{
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform Configs{
    vec2 Offset;
    vec2 HalfPixel;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord * 2.0;
    vec2 halfpixel = HalfPixel * 2.0;
    vec4 sum = texture(InSampler, uv) * 4.0;
    sum += texture(InSampler, uv - halfpixel * Offset);
    sum += texture(InSampler, uv + halfpixel * Offset);
    sum += texture(InSampler, uv + vec2(halfpixel.x, -halfpixel.y) * Offset);
    sum += texture(InSampler, uv - vec2(halfpixel.x, -halfpixel.y) * Offset);
    fragColor = sum / 8.0;
}