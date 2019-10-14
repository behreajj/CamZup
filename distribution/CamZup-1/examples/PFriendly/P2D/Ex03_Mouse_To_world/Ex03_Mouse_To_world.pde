import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;

float x = 0.0;
float y = 0.0;
Vec2 mouse = new Vec2();

void setup() {
  size(720, 405, "camzup.pfriendly.Yup2");
  graphics = (Yup2)getGraphics();
}


void draw() {
  surface.setTitle(String.format("%.1f", frameRate));

  // Jitter the camera.
  float angle = frameCount * 0.01;
  x += random(-1.0, 1.0);
  y += random(-1.0, 1.0);
  float s = lerp(0.5, 4.0, cos(angle) * 0.5 + 0.5);

  graphics.camera(x, y, angle, s, s);

  // Acquire the mouse after the camera has transformed
  // the scene; otherwise it will lag behind.
  graphics.mouse(mouse);

  background(#202020);
  graphics.grid(16, 4.0);
  stroke(#fff7d5);
  strokeWeight(10.0);
  point(mouse.x, mouse.y);
}
