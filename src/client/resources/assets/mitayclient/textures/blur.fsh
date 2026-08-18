#version 150

uniform sampler2D DiffuseSampler;
uniform float Radius;
uniform vec2 Dir;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    ivec2 texSize = textureSize(DiffuseSampler, 0);
    vec2 pixelSize = 1.0 / vec2(texSize);
    vec4 color = vec4(0.0);
    float totalWeight = 0.0;
    int intRadius = int(ceil(Radius));
    
    for (int i = -intRadius; i <= intRadius; i++) {
        float weight = exp(-float(i*i) / (2.0 * Radius * Radius));
        vec2 offset = Dir * float(i) * pixelSize;
        color += texture(DiffuseSampler, texCoord + offset) * weight;
        totalWeight += weight;
    }
    
    fragColor = color / totalWeight;
}