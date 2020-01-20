import camzup.pfriendly.*;
import camzup.core.*;

YupJ2 graphics;

float x = 0.0;
float y = 0.0;
Vec2 mouse = new Vec2();

void setup() {
  size(720, 405, "camzup.pfriendly.YupJ2");
  graphics = (YupJ2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

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
