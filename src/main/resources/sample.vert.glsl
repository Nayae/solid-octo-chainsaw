#version 330 core

layout (location = 0) in vec2 aOffset;
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec4 aPositions;

uniform int uMode;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

uniform float uCountX;
uniform float uCountY;
uniform vec2 uOffset;
uniform float uScale;
uniform float uLineThickness;

out vec3 oColor;

const vec3 triangleVertices[4] = vec3[4](
    vec3(0.0f, 3.0f, 0.0f),
    vec3(3.0f, 0.0f, 0.0f),
    vec3(0.0f, -3.0f, 0.0f),
    vec3(-3.0f, 0.0f, 0.0f)
);

const vec2 rectangleCornerMultipliers[4] = vec2[4](
    vec2(1.0f, 1.0f),
    vec2(-1.0f, -1.0f),
    vec2(-1.0f, -1.0f),
    vec2(1.0f, 1.0f)
);

void main()
{
    if (uMode == 0) {
        int currentColumn = gl_InstanceID % int(uCountX);
        int currentRow = int(gl_InstanceID / uCountX);

        float xOffset = currentColumn * uScale + mod(uOffset.x, uScale);
        float yOffset = currentRow * uScale + mod(uOffset.y, uScale);

        gl_Position = uProjection * uView * uModel * vec4(
            triangleVertices[gl_VertexID].x + xOffset,
            triangleVertices[gl_VertexID].y - yOffset,
            0.0f,
            1.0
        );

        oColor = vec3(0.6f, 0.6f, 0.6f);
    } else if (uMode == 1) {
        gl_Position = uProjection * uView * uModel * vec4(
            triangleVertices[gl_VertexID].x + aOffset.x * uScale + uOffset.x,
            triangleVertices[gl_VertexID].y - aOffset.y * uScale - uOffset.y,
            0.0f,
            1.0
        );

        oColor = aColor;
    } else if (uMode == 2) {
        vec2 normalised = normalize(vec2(-(aPositions.z - aPositions.x), (aPositions.w - aPositions.y)));

        // Relative coordinates are used, when multiplying postions in gl_Position with scale, it would also increase the width
        // Keep the line witdh absolute by dividing the relative thickness by the scale
        float lineWidth = uLineThickness * 0.5f;

        // Calculate the corner offset, based on the single line provided as input data
        // This effectively creates the thickness of a line
        vec2 cornerOffset = normalised * lineWidth * rectangleCornerMultipliers[gl_VertexID];

        // Calculate the index of the position data to use
        // gl_VertexID [0] = 0, [1] = 0, [2] = 1, [3] = 1
        int positionIndex = int(floor(float(gl_VertexID) * 0.5f)) * 2;

        // Calculate the relative position of the corner, this is still in grid coordinates
        vec2 position = vec2(aPositions[positionIndex] * uScale, aPositions[positionIndex + 1] * uScale);

        gl_Position = uProjection * uView * uModel * vec4(
            position.x + uOffset.x + cornerOffset.y,
            -position.y - uOffset.y - cornerOffset.x,
            0.0f,
            1.0
        );

        oColor = vec3(1.0f, 0.0f, 0.0f);
    }
}