#version 330 core

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

uniform float uCountX;
uniform float uCountY;
uniform vec2 uOffset;
uniform float uScale;

uniform sampler2D uPointColorTexture;

out vec3 iColor;

const vec3 vertices[4] = vec3[4](
    vec3(0.0f, 3.0f, 0.0f),
    vec3(3.0f, 0.0f, 0.0f),
    vec3(0.0f, -3.0f, 0.0f),
    vec3(-3.0f, 0.0f, 0.0f)
);

void main()
{
    int currentColumn = gl_InstanceID % int(uCountX);
    int currentRow = int(gl_InstanceID / uCountX);

    float xOffset = currentColumn * uScale + mod(uOffset.x, uScale);
    float yOffset = currentRow * uScale + mod(uOffset.y, uScale);

    gl_Position = uProjection * uView * uModel * vec4(vertices[gl_VertexID].x + xOffset, vertices[gl_VertexID].y - yOffset, 0.0f, 1.0);
    iColor = vec3(0.6f, 0.6f, 0.6f);
}