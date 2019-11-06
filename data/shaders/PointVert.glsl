uniform mat4 projectionMatrix;
uniform mat4 modelviewMatrix;
 
uniform vec4 viewport;
uniform int perspective; 
 
attribute vec4 position;
attribute vec4 color;
attribute vec2 offset;

varying vec4 vertColor;

void main() {
  vec4 pos = modelviewMatrix * position;
  vec4 clip = projectionMatrix * pos;
  vec2 perspScale = (projectionMatrix * vec4(1.0, 1.0, 0.0, 0.0)).xy;
  vec2 noPerspScale = clip.w / (0.5 * viewport.zw);

  gl_Position.xy = clip.xy + offset.xy * mix(noPerspScale, perspScale, float(perspective > 0));
  gl_Position.zw = clip.zw;
  
  vertColor = color;
}