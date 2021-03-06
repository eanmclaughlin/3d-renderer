#version 150 core

uniform sampler2D texMap;

in vec3 worldPos;
in vec3 worldNormal;
in vec2 texCoord;

out vec3 worldPosition;
out vec3 diffuseColor;
out vec3 normal;

void main(void) {
    worldPosition = worldPos;
    normal = normalize(worldNormal);
    diffuseColor = texture(texMap, texCoord).xyz;
}