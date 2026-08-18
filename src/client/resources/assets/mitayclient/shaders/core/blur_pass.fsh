#version 150

uniform sampler2D InSampler;
in vec2 texCoord;
out vec4 fragColor;

#define RADIUS 2.0

#ifdef HORIZONTAL
    #define DIR vec2(1.0, 0.0)
#else
    #define DIR vec2(0.0, 1.0)
#endif

void main() {
    vec2 texelSize = 1.0 / textureSize(InSampler, 0);
    vec2 dir = DIR * texelSize * RADIUS;
    vec4 color = texture(InSampler, texCoord);
    for (int i = 1; i <= 4; i++) {
        float weight = 1.0 / (1.0 + float(i));
        color += texture(InSampler, texCoord + dir * i) * weight;
        color += texture(InSampler, texCoord - dir * i) * weight;
    }
    fragColor = color / (1.0 + 2.0 * (1.0/2.0 + 1.0/3.0 + 1.0/4.0));
}