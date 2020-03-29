import camzup.pfriendly.*;
import camzup.core.*;

Yup2 graphics;

float x = 0.0;
float y = 0.0;
Vec2 mouse = new Vec2();

void settings() {
  size(720, 405, Yup2.PATH_STR);
}

void setup() {
  graphics = (Yup2)getGraphics();
}

void draw() {
  surface.setTitle(Utils.toFixed(frameRate, 1));

  // Jitter the camera.
  float angle = frameCount * 0.0025;
  x += random(-1.0, 1.0);
  y += random(-1.0, 1.0);
  float s = Utils.pingPong(0.5, 4.0, angle);

  graphics.camera(x, y, angle, s, s);

  // Acquire the mouse after the camera has transformed
  // the scene; otherwise it will lag behind.
  graphics.mouse(mouse);

  background(#202020);
  graphics.grid(16, 4.0);
  stroke(#fff7d5);
  strokeWeight(10.0);
  graphics.point(mouse);
}
