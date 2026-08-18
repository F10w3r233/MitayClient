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
    vec2 uv = texCoord * 0.5;
    vec2 halfpixel = HalfPixel * 0.5;
    vec4 sum = texture(InSampler, uv + vec2(-halfpixel.x * 2.0, 0.0) * Offset);
    sum += texture(InSampler, uv + vec2(-halfpixel.x, halfpixel.y) * Offset)* 2.0;
    sum += texture(InSampler, uv + vec2(0.0, halfpixel.y * 2.0) * Offset) * 2.0;
    sum += texture(InSampler, uv + halfpixel * Offset) * 2.0;
    sum += texture(InSampler, uv + vec2(halfpixel.x * 2.0, 0.0) * Offset);
    sum += texture(InSampler, uv + vec2(halfpixel.x, -halfpixel.y) * Offset) * 2.0;
    sum += texture(InSampler, uv + vec2(0.0, -halfpixel.y * 2.0) * Offset);
    sum += texture(InSampler, uv - halfpixel * Offset) * 2.0;
    fragColor = sum / 12.0;
}