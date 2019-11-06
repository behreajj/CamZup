uniform mat4 modelviewMatrix;
uniform mat4 projectionMatrix;

uniform vec4 viewport;
uniform int perspective;
uniform vec3 scale;

attribute vec4 position;
attribute vec4 color;
attribute vec4 direction;

varying vec4 vertColor;
  
void main() {
  vec4 posp = modelviewMatrix * position;
  vec4 posq = modelviewMatrix * (position + vec4(direction.xyz, 0.0));
  posp.xyz = posp.xyz * scale;
  posq.xyz = posq.xyz * scale;
  vec4 p = projectionMatrix * posp;
  vec4 q = projectionMatrix * posq;
  vec2 tangent = (q.xy * p.w - p.xy * q.w) * viewport.zw;

  tangent = dot(tangent, tangent) == 0.0 ? vec2(0.0, 0.0) : normalize(tangent);

  vec2 normal = vec2(-tangent.y, tangent.x);
  float thickness = direction.w;
  vec2 offset = normal * thickness;
  vec2 perspScale = (projectionMatrix * vec4(1.0, 1.0, 0.0, 0.0)).xy;

  vec2 noPerspScale = p.w / (0.5 * viewport.zw);

  gl_Position.xy = p.xy + offset.xy * mix(noPerspScale, perspScale, float(perspective > 0));
  gl_Position.zw = p.zw;

  vertColor = color;
}
