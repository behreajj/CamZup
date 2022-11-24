#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform vec4 aColor;
uniform vec4 bColor;
uniform vec2 origin;
uniform vec2 dest;

vec4 sRgbTolRgb(vec4 s, bool alpha) {
    float la = alpha ? (s.w <= 0.04045 ? s.w * 0.07739938 : pow((s.w + 0.055) * 0.9478673, 2.4)) : s.w;
    float lr = s.x <= 0.04045 ? s.x  * 0.07739938 : pow((s.x + 0.055) * 0.9478673, 2.4);
    float lg = s.y <= 0.04045 ? s.y * 0.07739938 : pow((s.y + 0.055) * 0.9478673, 2.4);
    float lb = s.z <= 0.04045 ? s.z * 0.07739938 : pow((s.z + 0.055) * 0.9478673, 2.4);
    return vec4(lr, lg, lb, la);
}

vec4 lRgbTosRgb(vec4 l, bool alpha) {
    float sa = alpha ? (l.w <= 0.0031308 ? l.w * 12.92 : pow(l.w, 0.41666666) * 1.055 - 0.055) : l.w;
    float sr = l.x <= 0.0031308 ? l.x * 12.92 : pow(l.x, 0.41666666) * 1.055 - 0.055;
    float sg = l.y <= 0.0031308 ? l.y * 12.92 : pow(l.y, 0.41666666) * 1.055 - 0.055;
    float sb = l.z <= 0.0031308 ? l.z * 12.92 : pow(l.z, 0.41666666) * 1.055 - 0.055;
    return vec4(sr, sg, sb, sa);
}

void main() {
    vec2 b = dest - origin;
    gl_FragColor = lRgbTosRgb(mix(
        sRgbTolRgb(aColor, false),
        sRgbTolRgb(bColor, false),
        clamp(dot(gl_FragCoord.xy - origin, b)
        / dot(b, b), 0.0, 1.0)),
        false);
}