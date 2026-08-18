#version 330

#moj_import <minecraft:globals.glsl>

uniform sampler2D InSampler;
uniform sampler2D FromProgramSampler;
uniform sampler2D BlurSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform Configs {
    vec4 winRect;
   float noisePercent;
};

in vec2 texCoord;

out vec4 fragColor;

float random(vec2 st){
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

void main() {
    if(texCoord.x < WinRect.x || texCoord.y < WinRect.y || texCoord.x >= WinRect.x + WinRect.z || texCoord.y >= WinRect.y + WinRect.w)
        fragColor = texture(InSampler, texCoord);
    else{
        vec4 color = texture(FromProgramSampler, vec2(
        (texCoord.x - WinRect.x) / WinRect.z,
        (1.0 - texCoord.y - WinRect.y) / WinRect.w));
        vec4 blurColor = texture(BlurSampler, texCoord);
        blurColor.rgb += vec3(random(texCoord) * 0.1 * noisePercent);
        fragColor = mix(texture(InSampler, texCoord), blurColor, color.r);
    }
}