#version 150
uniform sampler2D InSampler;
uniform float direction; // 0 = horizontal, 1 = vertical
in vec2 texCoord;
out vec4 fragColor;
void main() {
    vec2 texelSize = 1.0 / textureSize(InSampler, 0);
    vec2 offset = (direction == 0.0) ? vec2(texelSize.x, 0.0) : vec2(0.0, texelSize.y);
    float weights[5] = float[](0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
    vec4 color = texture(InSampler, texCoord) * weights[0];
    for (int i = 1; i < 5; i++) {
        color += texture(InSampler, texCoord + offset * i) * weights[i];
        color += texture(InSampler, texCoord - offset * i) * weights[i];
    }
    fragColor = color;
}