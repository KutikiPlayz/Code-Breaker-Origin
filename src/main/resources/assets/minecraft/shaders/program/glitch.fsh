#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;

uniform vec4 ColorModulate;

in vec2 texCoord;
out vec4 fragColor;

//2D (returns 0 - 1)
float random2d(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float randomRange (in vec2 seed, in float min, in float max) {
    return min + random2d(seed) * (max - min);
}

// return 1 if v inside 1d range
float insideRange(float v, float bottom, float top) {
    return step(bottom, v) - step(top, v);
}

//inputs
float AMT = 0.025; //0 - 1 glitch amount
float SPEED = 0.5; //0 - 1 speed

void main() {
    float time = floor(Time * SPEED * 60.0);

    //copy orig
    vec4 outCol = texture(DiffuseSampler, texCoord);

    //randomly offset slices horizontally
    float maxOffset = AMT / 2.0;
    for (float i = 0.0; i < 10.0 * AMT; i += 1.0) {
        float sliceY = random2d(vec2(time, 2345.0 + float(i)));
        float sliceH = random2d(vec2(time, 9035.0 + float(i))) * 0.25;
        float hOffset = randomRange(vec2(time , 9625.0 + float(i)), -maxOffset, maxOffset);
        vec2 texCoordOff = texCoord;
        texCoordOff.x += hOffset;
        if (insideRange(texCoord.y, sliceY, fract(sliceY + sliceH)) == 1.0) {
            outCol = texture(DiffuseSampler, texCoordOff);
        }
    }

    fragColor = outCol * ColorModulate;
}
